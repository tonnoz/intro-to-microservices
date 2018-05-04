const express = require("express");
const async = require('async');
const rest = require('unirest');
const app = express();
const RUNNING_ON_PORT = 3001;

const port_time = 3200;
const port_rand = 3300;

/** function to return safely the object data[name] from the data object **/
const uparse = function(data, name) {
    return data === undefined ? undefined : data[name];
}


/** GET endpoint for the Edge service: call in parallel the rand and time service and compose the return response **/
app.get("/", function(request, response) { 
    console.log("Request received from " + request.ip);

    async.parallel({
        //call the services that you want in parallel
    },
    function(err, results) {
        //compose response and return it (response.send(..))
    });
});

/** Booting of Express app **/
app.listen(RUNNING_ON_PORT, function() {
    console.log("Edge service started on port 3001");
});