/**
 * Created by shangchengcai on 2018/1/9.
 */
/**
 * Module dependencies.
 */
var ioredis = require('ioredis');
var redis   = require('redis');
var conf=require("../conf/arconf.json");
var logger=require("../util/LogUtil").LOGGER;
var client;

/**
 * config redis client by redis deploy type
 */
switch (conf.redisDeployType){
    case "single":
        var redisconf=conf.redisAddress[0];
        client=redis.createClient(redisconf.port, redisconf.host);
        break;
    case "cluster":
        client=new ioredis.Cluster(conf.redisAddress);
        break;
    default:
        throw new Error("redisDeployType only can be single or cluster");
        break;

}

/**
 * create redis client err
 */
client.on("error", function(error) {
    logger.error(error);
});

/**
 * get hash key fields
 */
function get(key,callback){
    client.hscan(key,0, function (err, res) {
        callback(err,res[1]);
    });
}

/**
 * remove hash key field
 */
function remove(key,field){
    client.hdel(key,field, function (err, res) {
        if(err){
            logger.info(err);
        }
        logger.info("remove faild host for key="+key+" and field="+field+" excute success!redis return code is "+(res===0?"faild,because redis aready don't contains this key!":"success!"));
    });
}

/**
 * close redis cluster client
 */
function close(){
    client.quit(function(){
        logger.info("close redis success!");
    });
}

/**
 * export get、remove、close
 */
exports.get=get;
exports.remove=remove;
exports.close=close;