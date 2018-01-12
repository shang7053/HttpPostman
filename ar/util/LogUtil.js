/**
 * Created by shangchengcai on 2018/1/12.
 */
/**
 * Module dependencies.
 */
var log4js = require('log4js');
var conf=require("../conf/arconf.json");

/**
 * config log4js from conf
 */
log4js.configure(conf.log4js);
const LOGGER = log4js.getLogger();


/**
 * add log4js to express app
 */
function add2App(app){
    app.use(log4js.connectLogger(LOGGER, {level:'auto'}));
}

/**
 * export logger,add2App
 */
exports.LOGGER = LOGGER;
exports.add2App = add2App;