const express = require("express");
const async = require('async');
const rest = require('unirest');
const app = express();
const RUNNING_ON_PORT = 3001;
const SERVICES_POLLING_INTERVAL_MSEC = 5000;
const REGISTRY_PORT = 3000;

var port_time; //the dynamically assigned port for the time service
var port_rand; //the dynamically assigned port for the rand service

/** function to return safely the object data[name] from the data object **/
const uparse = function(data, name) {
    return data === undefined ? undefined : data[name];
}


/** GET endpoint for the Edge service: call in parallel the rand and time service and compose the return response **/
app.get("/", function(request, response) { 
    console.log("Request received from " + request.ip);
    console.log("Will call \"time\" at port " + port_time)
    console.log("Will call \"rand\" at port " + port_rand)

    async.parallel({
        time: function(callback) {
            rest.get("http://localhost:"+ port_time).end(function(res) {
                callback(null, res.body);
            })
        },
        rand: function(callback) {
            rest.get("http://localhost:" + port_rand).end(function(res) {
                callback(null, res.body);
            })
        }
    },
    function(err, results) {
        if (!err) {
            response.send(
                "Hello stranger!" +
                "\n- today is " + uparse(results.time, "time") +
                "\n- your lucky number is " + uparse(results.rand, "number") +
                "\n");
            response.send();
        } else {
            console.log(err);
            response.send("Hello stranger!\n");
        }
    });
});

/** Booting of Express app **/
app.listen(RUNNING_ON_PORT, function() {
    console.log("Edge service started on port 3001");
});


/** Call the registry every SERVICES_POLLING_INTERVAL_MSEC mseconds and ask the best port where to find the time and rand service **/
setInterval(function() {
    console.log("calling registry to determine ports for time and rand service...")
    rest.get("http://localhost:"+ REGISTRY_PORT + "/time/").end(function(res) {
        port_time = res.body;
        console.log("registry replied me that the 'time service' is running at port : "+ port_time);
    });

    rest.get("http://localhost:"+ REGISTRY_PORT + "/rand/").end(function(res) {
        port_rand = res.body;
        console.log("registry replied me that the 'rand service' is running at port : "+ port_rand);
    });

}, SERVICES_POLLING_INTERVAL_MSEC);
