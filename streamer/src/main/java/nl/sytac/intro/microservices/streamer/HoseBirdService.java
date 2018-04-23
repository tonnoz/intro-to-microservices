package nl.sytac.intro.microservices.streamer;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class HoseBirdService {

    private Client hoseBirdClient;

    @Value("${consumerKey}")
    private String consumerKey;
    @Value("${consumerSecret}")
    private String consumerSecret;
    @Value("${token}")
    private String token;
    @Value("${secret}")
    private String secret;
    @Value("${maxQueueLength}")
    private int maxQueueLength;

    public List<String> giveMeTweets(String hashTag, Integer max, Integer timeout) throws InterruptedException {
        BlockingQueue<String> queue = connect2TwitterAndRetrieveTweets(hashTag);
        List<String> tweets = new ArrayList<>();
        long tStart = System.currentTimeMillis();
        while (!hoseBirdClient.isDone()) {
            String tweet = queue.take();
            tweets.add(tweet);
            log.error(tweet);
            long now = System.currentTimeMillis();
            if(tweets.size() >= max || (now - tStart) > timeout){
                break;
            }
        }
        return tweets;
    }

    private BlockingQueue<String> connect2TwitterAndRetrieveTweets(String hashTag) {
        BlockingQueue<String> outMessages = new LinkedBlockingQueue<>(maxQueueLength);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        List<String> terms = Lists.newArrayList(hashTag);
        hosebirdEndpoint.trackTerms(terms);
        final Hosts hoseBirdHosts = new HttpHosts(Constants.USERSTREAM_HOST);

        final Authentication hoseBirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
        final ClientBuilder builder = new ClientBuilder()
                .hosts(hoseBirdHosts)
                .authentication(hoseBirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(outMessages));

        hoseBirdClient = builder.build();
        hoseBirdClient.connect();
        return outMessages;
    }

    public Client getHoseBirdClient() {
        return hoseBirdClient;
    }

    public void connect() {
        log.debug("Connecting");
        hoseBirdClient.connect();
    }

    public void stop() {
        log.debug("Stopping");
        hoseBirdClient.stop();
    }

    public boolean isDone() {
        log.debug("Stopping");
        return hoseBirdClient.isDone();
    }

}