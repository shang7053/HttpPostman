var express = require('express');
var router = express.Router();
var request = require('request');
var superagent = require('superagent');
var redisUtil=require("../util/RedisUtil");

router.all('/*', function(req, res) {
    var type=req.method;
    var url=req.hostname;
    var time4Start=new Date().getTime();

    autoRouter(url,function(err,realUrl){
        if(err){
            res.render('error', { error: err });
            return;
        }
        try{
            var time4getRealUrl=new Date().getTime();
            if(null!==type&&undefined!==type&&""!==type){
                realUrl=realUrl+req.url;
                switch(type) {
                    case "GET":
                        handleReq(superagent.get(realUrl),req,res,time4Start,time4getRealUrl,url);
                        break;
                    case "POST":
                        handleReq(superagent.post(realUrl),req,res,time4Start,time4getRealUrl,url);
                        break;
                    case "PUT":
                        handleReq(superagent.put(realUrl),req,res,time4Start,time4getRealUrl,url);
                        break;
                    case "DELETE":
                        handleReq(superagent.del(realUrl),req,res,time4Start,time4getRealUrl,url);
                        break;
                    default:
                        res.render('error', { error: new Error('unsupert method type:'+type) });
                }
            }else{
                res.render('error', { error: new Error('can not find req method type !') });
            }
        }catch (rtErr){
            res.render('error', { error: rtErr });
        }
    });
});

function autoRouter(url,callback){
    redisUtil.get(url,function(err,data){
        var temp_err=null;
        var realUrl=null;
        try{
            if (err) {
                console.error(err);
            } else {
                realUrl=getRealUrl(data);
                if(!realUrl){
                    temp_err= new Error('no service registed!');
                }
                console.log("use "+realUrl+" for "+url+" request!")
            }
        }catch (autoRouterErr){
            temp_err=autoRouterErr;
        }
        callback(temp_err,realUrl);
    });
}

function getRealUrl(data){
    if(data.length===0){
        return null;
    }
    var total=0;
    for(var i=0;i<data.length;i++){
        if(i%2===1){
            total+=parseInt(data[i]);
        }
    }
    var random=Math.ceil(Math.random()*total);
    var weight=0;
    for(var i=0;i<data.length;i++){
        if(i%2===0){
            weight+=parseInt(data[i+1]);
            if(weight>=random){
                return data[i];
            }
        }
    }
    return null;
}

function handleReq(sreq,req,res,time4Start,time4getRealUrl,url){
    setHeader(req,sreq,true);
    sreq.send(req.body).end(function(err,sreqRes){
        if(err){
            res.render('error', { error: err });
        }else{
            setHeader(sreqRes,res,false);
            res.send(sreqRes.text);
        }
    });
    sreq.on('end', function(){
        var now=new Date().getTime();
        console.log("complete "+url+" request!total cost "+(now-time4Start)+" ms"+",and get realurl cost "+(time4getRealUrl-time4Start)+"ms,do real service cost "+(now-time4getRealUrl)+"ms");
    });
}

function setHeader(from,to,isRes){
    if (!isRes) {
        for (var x in from.headers) {
            if ("content-encoding" !== x.toLowerCase()) {
                to.set(x, from.headers[x]);
            }

        }
    } else {
        for (var x in from.headers) {
            if (x.toLowerCase() === "content-type") {
                if (from.headers[x].indexOf("form".toLowerCase()) > 0) {
                    to.type("form");
                } else if (from.headers[x].indexOf("json".toLowerCase()) > 0) {
                    to.type("json");
                } else if (from.headers[x].indexOf("html".toLowerCase()) > 0) {
                    to.type("html");
                } else {
                    to.type("form");
                }
            }
            if (x.toLowerCase() !== "host".toLowerCase() && x.toLowerCase() !== "content-length".toLowerCase() && x.toLowerCase() !== "content-type".toLowerCase()) {
                to.set(x, from.headers[x]);
            }
        }
    }
}
module.exports = router;
