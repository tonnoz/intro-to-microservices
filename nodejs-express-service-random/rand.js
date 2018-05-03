const express = require("express");
const rest = require('unirest');
const app = express();  
const port = (process.argv.length > 2) ? parseInt(process.argv[2],10) : 3300
const REGISTRY_POLLING_INTERVAL_MSEC = 5000;
const REGISTRY_PORT = 3000;

/** GET endpoint for Healthcheck (PING) **/
app.get("/ping", function(request, response) { 
    console.log("ping request received from registry");
    response.send("pong\n");        
});


/** GET endpoint (root) for the time service **/
app.get("/", function(request, response) {  
    var result = Math.round(Math.random() * 100)   
    response.json({
        number: result
    });
});

/** Booting of Express app **/
app.listen(port, function() {                       
    console.log("Rand service started on port "+port);
});


/** Communicates to the registry (via a POST) on which port I am running every
 * REGISTRY_POLLING_INTERVAL_MSEC milliseconds **/
setInterval(function() {
    console.log("informing registry I am alive on port " + port);
    rest.post("http://localhost:" + REGISTRY_PORT + "/rand/" + port).end();
}, REGISTRY_POLLING_INTERVAL_MSEC);