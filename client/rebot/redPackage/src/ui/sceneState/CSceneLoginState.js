Core.$Defines("Game.Const.RedPackageMsg")({
    Type:
    {
        "Read":"1",
        "WaitPackage":"2",
        "CiMsg":"3"
    }
});


Class({
    ClassName:"Game.SceneState.login",
    Base:"Game.SceneState.SceneStateBase",
    m_pTimer:null,
    Loader:null,
    onEnter:function()
    {
        this._super();
        var self = this;


        cc.loader.load(Game.Config.Res.Common, function(result, total,idx){

            },self.GameDataInit.bind(this));

    },
    onExit:function()
    {
        this._super();
        Client.removemap("packReadyRes",this);
        Client.removemap("CiPackageRes",this);
        Client.removemap("packageWaitRes",this);
    },
    GameDataInit:function()
    {
        Game.Config.init();

        Client.addmap("packReadyRes",this);
        Client.addmap("CiPackageRes",this);
        Client.addmap("packageWaitRes",this);
        this.onComplete();
    },


    onComplete:function()
    {
        var self = this;
        var scene = new cc.Scene();
        var ttf= new cc.LabelTTF("init ok...","",30);
        scene.addChild(ttf);
        ttf.x = scene.width/2;
        ttf.y = scene.height/2;
        cc.director.runScene(scene);
    },
    packReadyRes:function () {

    },

    CiPackageRes:function () {

    },
    packageWaitRes:function () {

    },

    UpdateMsg:function (type,str) {
        console.log("type:{0}------value:{1}".Format(type,str));
       var types = Game.Const.RedPackageMsg.Type;
        if(type == types.Read)
        {
            Server.packReady();
        }
        if(type == types.WaitPackage)
        {
            Server.packageWait();
        }
        else if(type == types.CiMsg)
        {
            var ay = str.split("&");
            var infos=[];
            for(var i=0;i<ay.length;i++)
            {
                var tempStr = ay[i];
                var oneInfo = tempStr.split("|");
                if(oneInfo.length >1)
                {
                    infos.push(oneInfo);
                }
            }
            Server.CiPackage(infos);
        }
    }
})
