const express = require("express");
const app = express();  
const DEFAULT_PORT = 3300;
const PORT = (process.argv.length > 2) ? parseInt(process.argv[2],10) : DEFAULT_PORT;
const SERVICE_NAME = 'rand';


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
