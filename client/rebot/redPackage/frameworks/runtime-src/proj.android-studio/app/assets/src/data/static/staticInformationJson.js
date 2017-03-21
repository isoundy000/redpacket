/**
 * Created by Administrator on 2016/11/29.
 */
Core.$Defines("Game.CStaticInformationClass")({
    addValueKey:function(key,value,map)
    {
        if( !map.$Length )
        {
            map.$Length=0;
        }
        map.$Length++;
        map[key] = value;
    },
    getJsonData:function(file)
    {
        return cc.loader.getRes("{0}/{1}.json".Format(Game.Config.Path.DataPath , file ));
    },
    Create: function(keys) {
        var data = this.getJsonData(keys[0]);
        var mainKey = keys[1];
        var value={};
        var map = {};
        for(var i=0;i<data.length;i++)
        {
            var temp = data[i];
            var tempMap = map;
            value[temp[mainKey]] = temp;
            var count = keys.length;
            for(var j=2;j<count;j++)
            {
                var key = temp[keys[j]];
                if(j === count-1)
                {
                    this.addValueKey(key,temp,tempMap);
                }
                else
                {

                    if(tempMap.hasOwnProperty(key))
                    {
                        tempMap = tempMap[key];
                    }
                    else
                    {
                        this.addValueKey(key,{},tempMap);
                        tempMap = tempMap[key];
                    }
                }
            }
        }
        map.$Value = value;
        return map;
    }
});

