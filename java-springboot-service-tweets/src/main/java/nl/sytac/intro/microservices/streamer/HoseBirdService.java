package nl.sytac.intro.microservices.streamer;

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

import static java.util.Collections.singletonList;

@Service
@Slf4j
public class HoseBirdService {


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

    private BlockingQueue<String> outMessages;
    private Client hoseBirdClient;
    private boolean initialized = false;

    public List<String> giveMeTweets(String hashTag, Integer maxTweets, Integer timeout) throws InterruptedException {
        return getTweets(hashTag, maxTweets, timeout);
    }

    public List<String> giveMeTweets(String hashTag) throws InterruptedException {
        return getTweets(hashTag, 50, 30000);
    }


    private List<String> getTweets(String hashTag, Integer maxTweets, Integer timeout) throws InterruptedException {
        init(hashTag);
        hoseBirdClient.connect();
        final List<String> tweets = new ArrayList<>();
        final long tStart = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        while (!hoseBirdClient.isDone() && sizeCondition(maxTweets, tweets) && timeCondition(timeout, tStart, now)) {
            final String tweet = outMessages.take();
            tweets.add(tweet);
            log.info(tweet);
            now = System.currentTimeMillis();
            Thread.sleep(1000);
        }
        return tweets;
    }

    public boolean init(String hashTag) {
        try{
            final Hosts hosts = new HttpHosts(Constants.USERSTREAM_HOST);
            final Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
            final StatusesFilterEndpoint hbcEndpoint = new StatusesFilterEndpoint().trackTerms(singletonList(hashTag));
            outMessages = new LinkedBlockingQueue<>(maxQueueLength);

            hoseBirdClient = new ClientBuilder()
                .hosts(hosts)
                .authentication(auth)
                .endpoint(hbcEndpoint)
                .processor(new StringDelimitedProcessor(outMessages))
                .build();

            initialized = true;
        }catch (Exception e){
            log.error("error during the initialization of the service", e);
            initialized = false;
        }
        return initialized;
    }

    private boolean timeCondition(Integer timeout, long tStart, long now) {
        return (now - tStart) <= timeout;
    }

    private boolean sizeCondition(Integer max, List<String> tweets) {
        return tweets.size() < max;
    }

}