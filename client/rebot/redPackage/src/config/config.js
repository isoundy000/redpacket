/**
 * Created by Administrator on 2016/11/29.
 */


Core.$Defines("Game.Config")({
    init:function()
    {
        Game.Data.init();
       var protocol = cc.loader.getRes("{0}/protocol.json".Format(this.Path.DataPath));
        Server.init(protocol["Client"]);
        Client.init(protocol["Server"]);
        Game.Data.GameLocalData.init();
    },
    Path:{
        DataPath:"res/data"
    },
    ScreenSize:{
        width:1280,
        height:720
    },
    Data: {

    },
    Res:{
        Common:["res/data/protocol.json"]
    }
});