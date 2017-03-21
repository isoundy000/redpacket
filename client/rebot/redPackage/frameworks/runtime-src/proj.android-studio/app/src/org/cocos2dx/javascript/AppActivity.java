/****************************************************************************
Copyright (c) 2015 Chukong Technologies Inc.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.javascript;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class AppActivity extends Cocos2dxActivity {

    Receiver m_Receiver = null;
    @Override
    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
        // TestCpp should create stencil buffer
        glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);



        m_Receiver = new Receiver();
        registerReceiver(m_Receiver, new IntentFilter("com.yhtgame.redPackage"));

        return glSurfaceView;
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(m_Receiver);
        super.onDestroy();
    }


    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg=intent.getStringExtra("msgContent");
            String msgType=intent.getStringExtra("msgType");
            String evalStr = String.format("Game.SceneState.CSceneStateFSM.Instance.CurrentSceneState.UpdateMsg(\"%s\",'%s')",msgType,msg);
            MsgToJs(evalStr);
        }
    }

    public  void MsgToJs(final String str)
    {
        this.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String strUTF8 = URLEncoder.encode(str, "UTF-8");
                    strUTF8 = strUTF8.replaceAll("\\+"," ");
                    String evalStr = String.format("eval(decodeURIComponent(\"%s\"))", strUTF8);
                    Cocos2dxJavascriptJavaBridge.evalString(evalStr);
                }
                catch (UnsupportedEncodingException e)
                {

                    System.out.println(str + " : " + e.toString());
                }
            }
        });

    }
}
