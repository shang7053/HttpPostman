/**
 * Created by shangchengcai on 2018/1/16.
 */
/**
 * Module dependencies.
 */
var zookeeper = require('node-zookeeper-client');
var conf=require("../conf/arconf.json");
var logger=require("../util/LogUtil").LOGGER;
/**
 * create zk client
 */
var client = zookeeper.createClient(conf.zkAddress);
var path =conf.zkBasePath || "/ar/domains";
var zkData={};

/**
 * watcher domain when domain CRUD,sync to zkData
 * @param client zk client
 * @param path basePath
 */
function watcherDomain(client, path) {
    client.getChildren(
        path,
        function (event) {
            logger.debug('Got domain watcher, path: %s', event.getPath());
            // go on Watcher the base path
            watcherDomain(client, path);
        },
        function (error, children) {
            if (error) {
                logger.debug('Failed to list domains of %s due to: %s.',path,error);
                return;
            }
            logger.debug('domains of %s are: %j.', path, children);
            for(var i=0;i<children.length;i++){
                watcherHost(client,path,children[i]);
                zkData[children[i]]=[];
                logger.debug('after set domains of zkData,the zkData is %s',JSON.stringify(zkData));
            }
        }
    );
}

/**
 *w atcher host of domain when host CRUD,sync to zkData
 * @param client zk client
 * @param basePath basePath
 * @param domain domain
 */
function watcherHost(client, basePath,domain) {
    var hostpath=basePath+"/"+domain;
    logger.debug("watcherHost path=%s",hostpath);
    client.getChildren(
        hostpath,
        function (event) {
            logger.debug('Got host watcher, path: %s', event.getPath());
            // go on Watcher the host path
            watcherHost(client, basePath,domain);
        },
        function (error, children) {
            logger.debug('hosts of %s are: %j.', hostpath, children);
            if (error) {
                logger.debug('Failed to list hosts of %s due to: %s.',hostpath,error);
            }
            zkData[domain]=undefined===children?[]:children;
            logger.debug('after set hosts of zkData,the zkData is %s',JSON.stringify(zkData));
        }
    );
}

/**
 * connect zk
 */
client.connect();

/**
 * init zkData
 */
function init(){
    client.once('connected', function () {
        logger.debug('Connected to ZooKeeper.');
        watcherDomain(client, path);
    });
}

/**
 * get hosts from zkData
 * @return a Array , all hosts of domain,like ['host:8080$1','host:80$1']
 */
function getHosts(domain){
    logger.debug("getHosts start,current zkData is %s",JSON.stringify(zkData));
    return zkData[domain];
}

/**
 * remove host from zkData
 * @return a Array , all hosts of domain,like ['host:8080$1','host:80$1']
 */
function remove(domain,host){
    logger.debug("remove zkData start,domain is %s,host is %s,current zkData is %s",domain,host,JSON.stringify(zkData));
    var hosts=zkData[domain];
    var index=-1;
    for(var i=0;i<hosts.length;i++){
        if(hosts[i].split("$")[0]===host){
            index=i;
            break;
        }
    }
    if(-1!==index){
        hosts.splice(index,1);
        zkData[domain]=hosts;
    }
    logger.debug("remove zkData success,domain is %s,host is %s,current zkData is %s",domain,host,JSON.stringify(zkData));
}

/**
 * close zk client
 */
function close(){
    logger.debug("close Connected to ZooKeeper.");
    client.close();
}

/**
 * export close、getHosts、init
 */
exports.close=close;
exports.getHosts=getHosts;
exports.init=init;
exports.remove=remove;