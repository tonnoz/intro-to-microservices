package nl.sytac.intro.microservices.streamer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisService {

    @Autowired
    private HoseBirdService hoseBirdService;

    private Jedis jedis = new Jedis("localhost");


    @Scheduled( initialDelay = 500, fixedDelay = 60000)
    public void refreshTwits() throws InterruptedException, JsonProcessingException {
        final TweetsWrapper tweetsWrapper = new TweetsWrapper(hoseBirdService.giveMeTweets("trump", 50, 30000));
        final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        jedis.set("tweets", objectWriter.writeValueAsString(tweetsWrapper));
    }
}
