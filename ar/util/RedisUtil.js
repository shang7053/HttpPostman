/**
 * Created by shangchengcai on 2018/1/9.
 */
/**
 * Module dependencies.
 */
var Redis = require('ioredis');
var conf=require("../conf/arconf.json");
var logger=require("../util/LogUtil").LOGGER;

/**
 * create redis cluster client
 */
var cluster = new Redis.Cluster(conf.redisAddress);

/**
 * get hash key fields
 */
function get(key,callback){
    cluster.hscan(key,0, function (err, res) {
        callback(err,res[1]);
    });
}

/**
 * remove hash key field
 */
function remove(key,field){
    cluster.hdel(key,field, function (err, res) {
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
    cluster.quit(function(){
        logger.info("close redis success!");
    });
}

/**
 * export get、remove、close
 */
exports.get=get;
exports.remove=remove;
exports.close=close;