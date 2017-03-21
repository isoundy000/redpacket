
Class({
    ClassName:"Game.Net.CWebSocket",
    state:0,
    pomelo:window.pomelo,
    IsOpened:function()
    {
        return this.state==1;
    },
    initPomelo:function()
    {
        var self = this;
        var map = {
            "close":self.close.bind(this),
            "disconnect":self.ondisconnect.bind(this),
            "io-error":this.onerror.bind(this),
            "reconnect":null,
            "error":self.onerror.bind(this),
            "onKick":function()
            {
                self.ondisconnect({code:1})
            },
            "heartbeat timeout":function()
            {
                self.ondisconnect({code:1000})
            }
        };

        for(var key in map)
        {
            if(map[key])
            {
                this.pomelo.removeListener(key,map[key]);
                this.pomelo.addListener(key,map[key]);
            }
        }
    },
    beginConnect:function(host,port,cb,t)
    {
        var self = this;
        var pomelo = self.pomelo;
        if(self.IsOpened())
        {
            Server.disconnect();
        }

        if(self.state == 0)
        {
            self.initPomelo();
            pomelo.init({
                host: host,
                port: port,
                log: true
            }, function() {
                self.state = 1;
                cb &&(t?cb.apply(t):cb());
            });

            pomelo.on("push",function(msg)
            {
                var key = null;
                for( key in msg)
                {
                    break;
                }
                console.log(JSON.stringify(msg))
                if(key)
                {
                    var c = msg[key];
                    self.onMessage(key,c);
                }

            })
            return;
        }
        Class.Assert(0,"server connected!");
    },
    disconect:function()
    {
        if(this.IsOpened())
        {
            var pomelo = this.pomelo;
            pomelo.removeAllListeners();
            pomelo.disconnect();
            this.close();
        }
    },

    Send:function(route,vMap,id,payloadName,cb,notify)
    {
        if(this.state == 1)
        {
            if(1 == notify)
            {
                this.pomelo.notify(route, vMap);
            }
            else
            {
                this.pomelo.request(route, vMap, function(data) {
                    Server.onmessage(data,id,payloadName,vMap,notify,cb);
                });
            }
        }
        else{
            Server.onfaildsend(notify);
        }
    },
    onMessage:function(key,data)
    {
        data.ID = key;
        data.payloadName = key;
        data.timestamp?0:data.timestamp = 0;
        Client.dispatch(data, null);
        delete data.ID;
        delete data.payloadName;
        delete data.timestamp;
    },
    ondisconnect:function()
    {

    },
    onerror:function()
    {

    },
    close:function()
    {
        this.state = 0;
    }
}).Static({
    Instance:Core.Instance
})