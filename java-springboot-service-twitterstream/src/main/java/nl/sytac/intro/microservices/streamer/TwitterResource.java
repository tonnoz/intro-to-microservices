package nl.sytac.intro.microservices.streamer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController()
@Slf4j
public class TwitterResource {

    @Autowired
    private HoseBirdService hoseBirdService;

    @GetMapping(value = "/tweets", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TweetsWrapper giveMeTweets(
                @RequestParam(value = "hashtag") String hashTag,
                @RequestParam(value = "maxTweets") Integer max,
                @RequestParam(value = "timeoutMsec") Integer timeout, HttpServletRequest request
            ) throws InterruptedException {

        log.info("received tweets request from {}:{}", request.getRemoteAddr(), request.getRemotePort());
        return new TweetsWrapper(hoseBirdService.giveMeTweets(hashTag, max, timeout));
    }

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String ping(HttpServletRequest request){
        log.info("received ping request from {}:{}", request.getRemoteAddr(), request.getRemotePort());
        return "pong\n";
    }
}
