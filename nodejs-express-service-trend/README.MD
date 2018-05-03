
# Trend Nodejs microService

This microservice fetches raw data from the [Google trend]("https://trends.google.com/trends/") Unofficial API, in a synchronous fashion.

exposed endpoint: 
```
/trend?hashtag=AN_HASHTAG
```

the service will return trending data per City for the last year using the interestByRegion function. Feel free to adapt it to your need by looking at the [documentation]("https://www.npmjs.com/package/google-trends-api#relatedqueries")

### Requires
Node, npm

### Installation

```sh
npm install

```


### Running
```sh
npm start PORT
```
If not specified, port 3700 will be used