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
        time: function(callback) {
            rest.get("http://localhost:" + port_time).end(function (res) {
                callback(null, res.body);
            });
        },
        rand: function(callback) {
            rest.get("http://localhost:" + port_rand).end(function (res) {
                callback(null, res.body);
            });
        }
    },
    function(err, results) {
        if (!err) {
            const message = "Hello stranger!" +
                (results.time ? "\n- today is " + uparse(results.time, "time") : "" ) +
                (results.rand ? "\n- your lucky number is " + uparse(results.rand, "number") : "")+"\n";
            response.send(message);
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