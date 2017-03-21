
Class({
    ClassName:"Game.SceneState.SceneStateBase",
    m_pTimer:null,
    m_pCacheInfo:null,
    Controllers:{},
    ScneneName:{
        get:function()
        {
           return this.ClassName.split(".")[2];
        }
    },

    ctor:function()
    {
        cc.director.runScene(new cc.Scene())
    },
    onEnter:function(cacheInfo)
    {
        // if(!this.m_pTimer)
        // {
        //     this.m_pTimer = setInterval(this.update,0.1,this);
        // }
        cc.director.runScene(new cc.Scene());
        this.m_pCacheInfo = cacheInfo;

    },
    onExit:function()
    {

       console.log("must overWrite onExit");
    },
    onComplete:function()
    {
       console.log("must overWrite onComplete");
    }
}).Static({
    Create:function()
    {
        return new this;
    }
})