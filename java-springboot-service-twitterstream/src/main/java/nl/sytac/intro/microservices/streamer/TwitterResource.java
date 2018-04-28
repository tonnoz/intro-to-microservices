package nl.sytac.intro.microservices.streamer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/tweet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TwitterResource {

    @Autowired
    private HoseBirdService hoseBirdService;

    @GetMapping
    public TweetsWrapper giveMeTweets(
                @RequestParam(value = "hashtag") String hashTag,
                @RequestParam(value = "max") Integer max,
                @RequestParam(value = "timeout") Integer timeout
            ) throws InterruptedException {

        return new TweetsWrapper(hoseBirdService.giveMeTweets(hashTag, max, timeout));
    }
}
