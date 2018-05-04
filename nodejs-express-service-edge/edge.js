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

    async.parallel({
        time: function(callback) {
             //call the service only if the port is available (have been communicated from the registry)

        },
        rand: function(callback) {
            //call the service only if the port is available (have been communicated from the registry)

        }
    },
    function(err, results) {
        if (!err) {
           response.send("compose your response here");
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
   // make a series of GET requests to the registry to determine on which port the services are running
   // and save it in the port_SERVICE_NAME variables.

}, SERVICES_POLLING_INTERVAL_MSEC);
