const express = require("express");
const rest = require('unirest');
const app = express();  
const DEFAULT_PORT = 3300;
const PORT = (process.argv.length > 2) ? parseInt(process.argv[2],10) : DEFAULT_PORT;
const REGISTRY_POLLING_INTERVAL_MSEC = 5000;
const REGISTRY_PORT = 3000;
const SERVICE_NAME = 'rand';

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
app.listen(PORT, function() {                       
    console.log(`${SERVICE_NAME} service started on port ${PORT}`);
});


/** Communicates to the registry (via a POST) on which port I am running **/
function callRegistry() {
    console.log("informing registry that I am alive on port " + PORT);
    rest.post(`http://localhost:${REGISTRY_PORT}/${SERVICE_NAME}/${PORT}`).end(function (response) {
        if(response.error) console.log(`Registry not available, trying again in ${REGISTRY_POLLING_INTERVAL_MSEC} msec`);
    });
}


/** Repeat every REGISTRY_POLLING_INTERVAL_MSEC milliseconds **/
setInterval(callRegistry, REGISTRY_POLLING_INTERVAL_MSEC);