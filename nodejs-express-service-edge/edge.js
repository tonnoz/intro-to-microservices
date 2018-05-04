const express = require("express");
const async = require('async');
const rest = require('unirest');
const app = express();
const RUNNING_ON_PORT = 3001;
const SERVICES_POLLING_INTERVAL_MSEC = 5000;
const REGISTRY_PORT = 3000;

var port_time; //the dynamically assigned port for the time service
var port_tweets; //the dynamically assigned port for the tweets service

/** function to return safely the object data[name] from the data object **/
const uparse = function(data, name) {
    return data === undefined ? undefined : data[name];
}



/** GET endpoint for the Edge service: call in parallel the tweets and time service and compose the return response **/
app.get("/", function(request, response) { 
    console.log("Request received from " + request.ip);

    async.parallel({
        time: function(callback) {
            if(port_time) { //call the service only if the port is available (has been communicated from the registry)
                rest.get("http://localhost:" + port_time).end(function (res) {
                    callback(null, res.body);
                });
            }else{
                console.warn("I was not able to call the time service since I am not aware of its running port");
                callback(null, null);
            }
        },
        tweets: function(callback) {
            if(port_tweets) { //call the service only if the port is available (has been communicated from the registry)
                rest.get("http://localhost:" + port_tweets).end(function (res) {
                    callback(null, res.body);
                });
            }else{
                console.warn("I was not able to call the tweets service since I am not aware of its running port");
                callback(null, null);
            }
        }
    },
    function(err, results) {
        if (!err) {
            const message = "Hello stranger!" +
                (results.time ? "\n- today is " + uparse(results.time, "time") : "" ) +
                (results.tweets ? "tweets:"+ results.tweets : "")+"\n";
            response.send(message);
        } else {
            console.log(err);
            response.send("Hello stranger!\n");
        }
    });
});

/** Booting of Express app **/
app.listen(RUNNING_ON_PORT, function() {
    console.log(`Edge service started on port ${RUNNING_ON_PORT}`);
});


/** Call the registry every SERVICES_POLLING_INTERVAL_MSEC mseconds and ask the best port where to find the time and tweets service **/
setInterval(function() {
    console.log("calling registry to determine ports for time and tweets service...")
    rest.get("http://localhost:"+ REGISTRY_PORT + "/time/").end(function(res) {
        port_time = res.body;
        console.log("registry replied me that the 'time service' is running at port : "+ port_time);
    });

    rest.get("http://localhost:"+ REGISTRY_PORT + "/tweets/").end(function(res) {
        port_tweets = res.body;
        console.log("registry replied me that the 'tweets service' is running at port : "+ port_tweets);
    });

}, SERVICES_POLLING_INTERVAL_MSEC);
