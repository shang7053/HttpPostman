/**
 * Module dependencies.
 */
var express = require('express');
var path = require('path');
var logger = require('morgan');
var bodyParser = require('body-parser');
var exphbs  = require('express-handlebars');
var multer = require('multer');
var ar = require('./routes/arer');//core router
var logUtil=require("./util/LogUtil");//log

/**
 * express app
 */
var app = express();

/**
 * config log
 */
logUtil.add2App(app);
app.use(logger('dev'));

/**
 * config templete engine
 */
app.engine('.hbs', exphbs({extname:'.hbs'}));
app.set('view engine', '.hbs');

/**
 * config request body
 */
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(multer()); // for parsing multipart/form-data

/**
 * config core router
 */
app.use('/', ar);

/**
 * catch 404 and forward to error handler
 */
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

/**
 * error handler
 */
app.use(function(err, req, res) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render('error');
});

/**
 *exports app
 */
module.exports = app;
