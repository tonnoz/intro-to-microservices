const express = require("express");
const app = express();
const DEFAULT_PORT = 3200;
const PORT = (process.argv.length > 2) ? parseInt(process.argv[2],10) : DEFAULT_PORT;
const SERVICE_NAME = 'time';



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