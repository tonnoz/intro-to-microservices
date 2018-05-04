const express = require("express");
const rest = require('unirest');
const googleTrends = require('google-trends-api');
const app = express();  
const DEFAULT_PORT = 3700;
const PORT = (process.argv.length > 2) ? parseInt(process.argv[2],10) : DEFAULT_PORT;
const SERVICE_NAME = 'trend';



/** GET endpoint (root) for the service **/
app.get(`/${SERVICE_NAME}`, function(request, response) {
    const keyword = request.query.hashtag;
    googleTrends.interestByRegion({keyword: keyword, startTime: new Date('2018-01-01'), endTime: new Date('2018-05-01'), resolution: 'CITY'})
        .then((res) => {
            response.json({
                'trend' : res
            });
        }).catch((err) => {
            response.json({
                'error' : err
            });
        });
});

/** Booting of Express app **/
app.listen(PORT, function() {                       
    console.log(`${SERVICE_NAME} service started on port ${PORT}`);
});