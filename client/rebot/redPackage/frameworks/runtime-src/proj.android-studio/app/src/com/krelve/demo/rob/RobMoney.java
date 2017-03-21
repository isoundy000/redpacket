package krelve.demo.rob;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;





import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



//import Android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.cocos2dx.javascript.AppActivity;

import java.util.concurrent.Future;
//import com.koushikdutta.async.http.AsyncHttpClient;
//import com.koushikdutta.async.http.AsyncHttpPost;
//import com.koushikdutta.async.http.AsyncHttpResponse;
//import com.koushikdutta.async.http.body.MultipartFormDataBody;
//import com.koushikdutta.async.http.callback.HttpConnectCallback;

public class RobMoney extends AccessibilityService {

	static boolean checkPackage = false;
	static boolean initTimer = true;
	static ArrayList PackValueList     =   new   ArrayList();
//	static ArrayList<AccessibilityNodeInfo> packageList = new   ArrayList();
	static int tickIndex = 0;
	static AccessibilityNodeInfo closeBtn = null;
	static AccessibilityNodeInfo mCurrentPacket = null;
	static int mLastCurrentPacketID = 0;
	static AccessibilityNodeInfo mCurrentPacketParent = null;
	static boolean RecycleParentEnd = false;

	static final String Type_Ready = "1";
	static final String Type_WaitPackage = "2";
	static final String Type_CiMsg = "3";

	String m_pState = Type_Ready;

	Runnable runnable = new Runnable() {
		public void run() {
			// task to run goes here
			if(m_pState == Type_CiMsg)
			{
				tickIndex++;
				Log.d("demo","Type_CiMsg:"+tickIndex);
				if(tickIndex%21 == 20)
				{
					ChangeState(Type_Ready,"");
				}
			}
			else if(m_pState == Type_WaitPackage)
			{
				tickIndex++;
				Log.d("demo","Type_WaitPackage:"+tickIndex);
				if(tickIndex%31 == 30)
				{
					relayOpenPacket();
				}
				if(tickIndex == 100)
				{
					ChangeState(Type_Ready,"");
				}
			}

			else if(m_pState == Type_Ready)
			{
				Log.d("demo","Type_Ready");
				tickIndex=0;
//				tickIndex++;
//				RecycleParentEnd = false;
//				if(tickIndex%2 == 0)
//				{
//					tickIndex=0;
//					getPacket();
//				}

			}

		}
	};
	@SuppressLint("NewApi")
	public  void sendMsg(String type,String path)
	{

		final Intent intent=new Intent("com.yhtgame.redPackage");
		intent.putExtra("msgContent", path);
		intent.putExtra("msgType", type);
		sendBroadcast(intent);
		return;

	}


	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {


		if(initTimer)
		{
			initTimer=false;
			mLastCurrentPacketID = 0;
			ScheduledExecutorService service = Executors
					.newSingleThreadScheduledExecutor();
			// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
			service.scheduleAtFixedRate(runnable, 3, 1, TimeUnit.SECONDS);
		}

		int eventType = event.getEventType();
		switch (eventType) {
		//第一步：监听通知栏消息
		case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
			List<CharSequence> texts = event.getText();
			if (!texts.isEmpty()) {
				for (CharSequence text : texts) {
					String content = text.toString();
					if (content.contains("[微信红包]")) {
						//模拟打开通知栏消息
						if (event.getParcelableData() != null
								&&
							event.getParcelableData() instanceof Notification) {
							Notification notification = (Notification) event.getParcelableData();
							PendingIntent pendingIntent = notification.contentIntent;

							Log.d("demo","微信红包:");

							try {
								pendingIntent.send();
							} catch (CanceledException e) {
								e.printStackTrace();
							}
//							ChangeState(Type_WaitPackage,"");

						}
					}
				}
			}
			break;
		//第二步：监听是否进入微信红包消息界面
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

			String className = event.getClassName().toString();
			Log.d("demo",className);
			if (className.equals("com.tencent.mm.ui.LauncherUI")) {
				//开始抢红包
				getPacket();
			}
			else if(className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")  )
			{
				if(checkPackage)
					return;
				checkPackage = true;
				getPackList();
			}
		else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI") || className.indexOf("com.tencent.mm.plugin.luckymoney.ui.") != -1 ) {
			//开始打开红包
			openPacket();
			Log.d("demo","openPacket");
		}
			break;
		}
	}

	@SuppressLint("NewApi")
	private void getPackList()
	{
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		recyclePackList(rootNode);

		String value = "";
		for(int i=0;i<PackValueList.size();i++)
		{
			value +=(PackValueList.get(i)+"&");
		}
		ChangeState(Type_CiMsg,value);
		if(closeBtn != null)
		{
			closeBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			closeBtn = null;
		}
	}

	@SuppressLint("NewApi")
	private void asyOnePackInfo(AccessibilityNodeInfo info)
	{
		String value="";
		for (int i = 0; i < info.getChildCount(); i++) {
			AccessibilityNodeInfo node = info.getChild(i);
			if(node!=null){
				if(node.getText() != null){
					if(value.length()!=0)
					{
						value+="|"+node.getText();
					}
					else
					{
						value+=node.getText();
					}
				}
			}
		}
		PackValueList.add(value);
	}
	@SuppressLint("NewApi")
	private void recyclePackList(AccessibilityNodeInfo info)
	{

		if("android.widget.ImageView".equals(info.getClassName().toString()))
		{

			closeBtn = info.getParent();
		}
		if (info.getChildCount() == 0) {
			if(info.getText() != null){
				String valueStr = info.getText().toString();

				if(valueStr.indexOf("元") != -1 && valueStr.length()<7)//
				{
					asyOnePackInfo(info.getParent());
				}

			}

		} else {
			for (int i = 0; i < info.getChildCount(); i++) {
				if(info.getChild(i)!=null){
					recyclePackList(info.getChild(i));
				}
			}
		}
	}
	/**
	 * 查找到
	 */
	@SuppressLint("NewApi")
	private void openPacket() {
		AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
		if (nodeInfo != null) {
			List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("看看大家的手气");
			for (AccessibilityNodeInfo n : list) {
				n.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		}

	}

	@SuppressLint("NewApi")
	private void getPacket() {

		{
			AccessibilityNodeInfo rootNode = getRootInActiveWindow();
			recycle(rootNode);

			if(mCurrentPacket != null)
				delayGetPackage();
		}
	}


	/**
	 * 打印一个节点的结构
	 * @param info
	 */
	@SuppressLint("NewApi")
	public void recycle(AccessibilityNodeInfo info) {
//		if(mCurrentPacket != null)
//			return;

        if (info.getChildCount() == 0) { 
        	if(info.getText() != null){
				String infoName = info.getText().toString();
        		if("查看红包".equals(infoName) || "领取红包".equals(infoName) ){
        			//这里有一个问题需要注意，就是需要找到一个可以点击的View


					mCurrentPacket = info;
					mLastCurrentPacketID = info.getWindowId();
					mCurrentPacketParent = getRootInActiveWindow();//info.getParent().getParent().getParent().getParent().getParent().getParent();
					Log.d("demo","init mLastCurrentPacketID:"+mLastCurrentPacketID);
//					delayGetPackage();
            	}
        	}
        	
        } else {  
            for (int i = 0; i < info.getChildCount(); i++) {  
                if(info.getChild(i)!=null){  
                    recycle(info.getChild(i));  
                }  
            }  
        }  
    }

	@SuppressLint("NewApi")
	public void recycleParent(AccessibilityNodeInfo info) {
		if(RecycleParentEnd )
			return;

		if (info.getChildCount() == 0) {
			if(info.getText() != null){
				String infoName = info.getText().toString();
				if("查看红包".equals(infoName) || "领取红包".equals(infoName)  ){
					//这里有一个问题需要注意，就是需要找到一个可以点击的View
					Log.d("demo","mLastCurrentPacketID:"+mLastCurrentPacketID+ "   info winodid"+info.getWindowId());
					if(mLastCurrentPacketID == info.getWindowId())
					{
						Log.d("demo","recycleParent same");
						return;
					}
					else
					{
						RecycleParentEnd = true;
						mCurrentPacket = info;
						mLastCurrentPacketID = info.getWindowId();
						Log.d("demo","not same same:"+mLastCurrentPacketID);
						delayGetPackage();
					}

				}
			}

		}
		else
		{
			for (int i = info.getChildCount()-1; i >-1; i--)
			{
				AccessibilityNodeInfo temp = info.getChild(i);
				Log.d("demo","getChildCount:"+temp.getClassName().toString());
				if(temp!=null && !temp.getClassName().toString().equals("android.widget.FrameLayout"))
				{
					Log.d("demo","recycleParent:"+i);
					recycleParent(temp);
				}
			}
		}
	}
	@Override
	public void onInterrupt() {
	}


	@SuppressLint("NewApi")
	public void delayGetPackage()
	{
		if(m_pState == Type_Ready)
			ChangeState(Type_WaitPackage,"");
	}

	@SuppressLint("NewApi")
	public void relayOpenPacket()
	{
		if(null == mCurrentPacket)
		{
			ChangeState(Type_Ready,"");
		}
		else
		{
			AccessibilityNodeInfo info = mCurrentPacket;
			info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			AccessibilityNodeInfo parent = info.getParent();
			while(parent != null){
				if(parent.isClickable()){
					parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					return;
				}
				parent = parent.getParent();
			}
		}
	}


	@SuppressLint("NewApi")
	public void ChangeState(String state,String value )
	{

		if(m_pState == state)
			return;
		m_pState = state;
		if(state == Type_Ready)
		{
			performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
			checkPackage=false;
			PackValueList.clear();
			mCurrentPacket = null;
			Log.i("demo", "reset openui value");
			sendMsg(Type_Ready,"");

		}
		if(state == Type_WaitPackage)
		{
			tickIndex = 0;
			sendMsg(Type_WaitPackage,"");
		}
		if(state == Type_CiMsg)
		{

			tickIndex = 0;
			sendMsg(Type_CiMsg,value);
		}
	}

}
