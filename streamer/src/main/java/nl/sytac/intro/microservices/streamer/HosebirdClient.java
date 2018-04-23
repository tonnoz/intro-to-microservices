import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//@PropertySource("classpath:application.properties")
public class HosebirdClient {

    private Client hosebirdClient;


    @Value("${consumerKey}")
    private String consumerKey;
    @Value("${consumerSecret}")
    private String consumerSecret;
    @Value("${token}")
    private String token;
    @Value("${secret}")
    private String secret;
    @Value("${maxQueueLength}")
    private static int maxQueueLength;

    public HosebirdClient(final BlockingQueue<String> msgQueue) {
        final UserstreamEndpoint userEndpoint = new UserstreamEndpoint();
        userEndpoint.withUser(true);
        final Hosts hosebirdHosts = new HttpHosts(Constants.USERSTREAM_HOST);

        final Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
        final ClientBuilder builder = new ClientBuilder()
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(userEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        hosebirdClient = builder.build();
    }

    public void connect() {
        hosebirdClient.connect();
    }

    public void stop() {
        hosebirdClient.stop();
    }

    public boolean isDone() {
        return hosebirdClient.isDone();
    }

    public static void main(String[] args) {
        final BlockingQueue<String> outMessages = new LinkedBlockingQueue<>(maxQueueLength);
        HosebirdClient hsb = new HosebirdClient()
        hsb.connect();
    }
}