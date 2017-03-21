/**
 * Created by Class on 2017/3/17.
 */
var PORT = 9801;

var http = require('http');
var url=require('url');
var resault={
    state:0,
    package:null
};


function resultBack(res)
{
    return function(result)
    {
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Headers", "X-Requested-With");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        res.setHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
        res.writeHead(200, "OK", {'Content-Type': 'text/html'});
        res.end(JSON.stringify(result));

    }
};
var server = http.createServer(function (request, response) {
    var urlInfo = url.parse(decodeURI(request.url),true);
    var reqInfo = urlInfo.query;
    var pathname = urlInfo.pathname;
    var resback = resultBack(response);
    if(pathname == "/packready")
    {
        resault.state = 0;
        resback({});
        return;
    }
    if(pathname == "/packagewait")
    {
        var packageInfo = reqInfo.info;
        resault.state = 1;
        resback({});
        return;
    }
    if(pathname == "/cipackage")
    {
        var packageInfo = reqInfo.info;
        resault.package = JSON.parse(packageInfo);
        resault.state = 2;
        resback({});
        return;
    }
    if(pathname == "/getpackinfo")
    {

        resback(resault);
        return;
    }

    response.end("0");
    return;

});
server.listen(PORT);
console.log("Server runing at port: " + PORT + ".");