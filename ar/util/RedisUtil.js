/**
 * Created by shangchengcai on 2018/1/9.
 */
// 注意，不一样的模块
var Redis = require('ioredis');

// 不一样的创建方式，多台获取，出来就是集群
var cluster = new Redis.Cluster(
    [{
        port: 6379,
        host: '172.16.40.4'
    }, {
        port: 6380,
        host: '172.16.40.4'
    }, {
        port: 6381,
        host: '172.16.40.4'
    }]
);
function get(key,callback){
    cluster.hscan(key,0, function (err, res) {
        callback(err,res[1]);
    });
}

exports.get=get;