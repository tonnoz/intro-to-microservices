package nl.sytac.intro.microservices.streamer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class RedisService {

    @Autowired
    private HoseBirdService hoseBirdService;
    private Jedis jedis = new Jedis("localhost");
    private ObjectMapper mapper = new ObjectMapper();


    @PostConstruct
    private void init(){
        jedis.flushAll();
    }

    @Scheduled(initialDelay = 500, fixedDelay = 60000)
    public void refreshTwits() throws InterruptedException, JsonProcessingException {
        final Set<String> keys = jedis.keys("*");
        final ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();
        for(String hashtag : keys){
            final List<String> tweets = hoseBirdService.giveMeTweets(hashtag, 50, 30000);
            final TweetsWrapper tweetsWrapper = new TweetsWrapper(tweets);
            jedis.set(hashtag, objectWriter.writeValueAsString(tweetsWrapper));
        }
    }

    public TweetsWrapper getTweets(String hashtag) throws InterruptedException, IOException {
        final String hashtag1 = jedis.get(hashtag);
        if(hashtag1 == null){ //hanging call
            return new TweetsWrapper(hoseBirdService.giveMeTweets(hashtag));
        }
        return mapper.readValue(hashtag1, TweetsWrapper.class);
    }
}
