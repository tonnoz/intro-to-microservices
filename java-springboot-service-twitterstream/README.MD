
# Java microService

This microservice fetches raw data from the [Userstream]("https://userstream.twitter.com") TwitterStream API, in a synchronous fashion.

exposed endpoint: 
```
/tweets?hashtag=AN_HASHTAG&maxTweets=MAX_NR_OF_TWEETS&timeoutMsec=TIMEOUT_IN_MSEC
```

the service will fetch tweets for the specified `hashtag` for max `maxTweets` tweets or `timeoutMsec` milliseconds whatever comes first and return them.

### Requires
Jdk8, Maven

### Installation

```sh
mvn clean install

```

then in your resources folder place a `application.properties` file like so:

```
#twitter access keys
consumerKey = YOURCONSUMERKEY
consumerSecret = YOURCONSUMERSECRET
token = YOURTOKEN
secret = YOURSECRET

# max expected buffer size
maxQueueLength = 100
#the port where your local service discovery (registry) is running
registryPort = 3000
#the name of this service
seviceName = YOURSERVICENAME
```

you can get your keys at:

https://apps.twitter.com/

you will need to create a new app.

### Running
```sh
mvn spring-boot:run -Dserver.port=YOUR_PORT
```
If not specified, port 8080 will be used