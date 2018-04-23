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

import javax.annotation.meta.When;
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

    private boolean done = false;

    private BlockingQueue<String> fetchTweets() {
        BlockingQueue<String> outMessages = new LinkedBlockingQueue<>(maxQueueLength);
        if (hoseBirdClient == null) {
            init(outMessages);
        }
        hoseBirdClient.connect();
        return outMessages;
    }

    public List<String> giveMeTweets() throws InterruptedException {
        List<String> tweets = new ArrayList<>();
        BlockingQueue<String> queue = fetchTweets();
        long tStart = System.currentTimeMillis();
        while (!hoseBirdClient.isDone() || done) {
            String tweet = queue.take();
            tweets.add(tweet);
            log.error(tweet);
            long now = System.currentTimeMillis();
            if((now - tStart) / 1000.0 > 30){
                done = true;
            }
        }
        return tweets;
    }

    private void init(final BlockingQueue<String> msgQueue) {
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        List<String> terms = Lists.newArrayList("trump");
        hosebirdEndpoint.trackTerms(terms);
        final Hosts hoseBirdHosts = new HttpHosts(Constants.USERSTREAM_HOST);

        final Authentication hoseBirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
        final ClientBuilder builder = new ClientBuilder()
                .hosts(hoseBirdHosts)
                .authentication(hoseBirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        hoseBirdClient = builder.build();
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