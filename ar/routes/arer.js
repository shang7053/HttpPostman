/**
 * Created by shangchengcai on 2018/1/08.
 */
/**
 * Module dependencies.
 */
var express = require('express');
var router = express.Router();
var request = require('request');
var superagent = require('superagent');
var redisUtil=require("../util/RedisUtil");
var conf=require("../conf/arconf.json");
var logger=require("../util/LogUtil").LOGGER;

/**
 * match all request
 */
router.all('/*', function(req, res) {
    autoRouter(req, res,routerHost);
});

/**
 * do auto match request
 * @param req http request
 * @param res http response
 * @param callback how to do when get a realdomain
 */
function autoRouter(req, res,callback){
    var time4Start=new Date().getTime();
    var req_domain=req.hostname;
    redisUtil.get(req_domain,function(err,data){
        var temp_err=err;
        var host=null;
        try{
            if (err) {
                logger.error(err);
            } else {
                host=getHost(data);
                if(!host){
                    temp_err= new Error('no registed service host!');
                }
            }
        }catch (autoRouterErr){
            logger.error(autoRouterErr);
            temp_err=autoRouterErr;
        }
        //var type=req.method;
        callback(temp_err,req,res,host,time4Start);
    });
}

/**
 * router to real host
 * @param err the err when get hsot
 * @param req http request
 * @param res http response
 * @param host the host where generate by weight
 * @param time4Start The time for this request start
 */
function routerHost(err,req,res,host,time4Start){
    if(err){
        logger.error(err);
        res.render('error', { error: err });
        return;
    }
    try{
        var type=req.method;
        if(null!==type&&undefined!==type&&""!==type){
            var serverUrl=host+req.url;
            switch(type) {
                case "GET":
                    handleReq(superagent.get(serverUrl),req,res,time4Start,host);
                    break;
                case "POST":
                    handleReq(superagent.post(serverUrl),req,res,time4Start,host);
                    break;
                case "PUT":
                    handleReq(superagent.put(serverUrl),req,res,time4Start,host);
                    break;
                case "DELETE":
                    handleReq(superagent.del(serverUrl),req,res,time4Start,host);
                    break;
                default:
                    res.render('error', { error: new Error('unsupert method type:'+type) });
            }
        }else{
            res.render('error', { error: new Error('can not find req method type !') });
        }
    }catch (rtErr){
        logger.error(rtErr);
        res.render('error', { error: rtErr });
    }
}

/**
 * get host from redis data,will use weight
 * @param data redis data like [host1,1,host2,1]
 * @returns {*} host
 */
function getHost(data){
    try{
        if(data.length===0){
            return null;
        }
        var totalWeight=0;
        // get total weight
        for(var i=0;i<data.length;i++){
            if(i%2===1){
                totalWeight+=parseInt(data[i]);
            }
        }
        // generate random by weight
        var random=Math.ceil(Math.random()*totalWeight);
        // choose host from data by weight
        var weight=0;
        for(var i=0;i<data.length;i++){
            if(i%2===0){
                weight+=parseInt(data[i+1]);
                if(weight>=random){
                    return data[i];
                }
            }
        }
    }catch (err){
        throw err;
    }
    return null;
}

/**
 * deal request and log something
 * @param sreq superagent instance
 * @param req http request
 * @param res http response
 * @param time4Start The time for this request start
 * @param host the host where generate by weight
 */
function handleReq(sreq,req,res,time4Start,host){
    // The time for this request get host,but this time > real cost time,so it just for reference
    var time4getRealUrl=new Date().getTime();
    var req_domain=req.hostname;
    // first set header
    setHeader(req,sreq,true);
    //send resquest
    sreq.send(req.body).timeout(conf.httpTomeOut?conf.httpTomeOut:3000).end(function(err,sreqRes){
        if(err){
            logger.error(err);
            // when err remove unreachable host
            redisUtil.remove(req_domain,host);
            if(conf.polling){
                // Polling all host up to success,if all faild return err-no registed service host!
                autoRouter(req, res,routerHost);
            }else{
                // fast err
                res.render('error', { error: err });
            }
        }else{
            setHeader(sreqRes,res,false);
            res.send(sreqRes.text);
        }
    });
    // log something
    sreq.on('end', function(){
        var now=new Date().getTime();
        logger.info("complete "+req_domain+" request,use "+host+"! total cost "+(now-time4Start)+" ms"+",and get realurl cost "+(time4getRealUrl-time4Start)+"ms,do real service cost "+(now-time4getRealUrl)+"ms");
    });
}

/**
 * set request or response header
 * @param from the header from
 * @param to to header to
 * @param isRes request and response need different header,so sed to distinguish,when true it's request,false response
 */
function setHeader(from,to,isRes){
    if (!isRes) {
        //when response, del header content-encoding,because it will make this request none response
        for (var x in from.headers) {
            if ("content-encoding" !== x.toLowerCase()) {
                to.set(x, from.headers[x]);
            }

        }
    } else {
        //when request, del header host,content-length,content-type, because it will make this request dead cycle,final match content-type to json or form or html
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

/**
 * export router
 */
module.exports = router;
