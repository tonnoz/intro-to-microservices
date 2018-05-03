const express = require("express");
const rest = require('unirest');
const app = express();
const DEFAULT_PORT = 3200;
const PORT = (process.argv.length > 2) ? parseInt(process.argv[2],10) : DEFAULT_PORT;
const REGISTRY_POLLING_INTERVAL_MSEC = 5000;
const REGISTRY_PORT = 3000;
const SERVICE_NAME = 'time';


/** GET endpoint for Healthcheck (PING) **/
app.get("/ping", function(request, response) {
    console.log("ping request received from registry");
    response.send("pong\n");
});


/** GET endpoint (root) for the time service **/
app.get("/", function(request, response) {
    var now = new Date().toISOString();
    response.json({
        time: now
    });
});

/** Booting of Express app **/
app.listen(PORT, function() {
    console.log(`${SERVICE_NAME} service started on port ${PORT}`);
});

/** Communicates to the registry (via a POST) on which port I am running **/
function callRegistry() {
    console.log("informing registry that I am alive on port " + PORT);
    rest.post(`http://localhost:${REGISTRY_PORT}/time/${PORT}`).end()
}


/** Repeat every REGISTRY_POLLING_INTERVAL_MSEC milliseconds **/
setInterval(callRegistry, REGISTRY_POLLING_INTERVAL_MSEC);