package nl.sytac.intro.microservices.streamer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/tweet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TwitterEndpoint {

    @Autowired
    private HoseBirdService hoseBirdService;

    @GetMapping
    public List<String> giveMeTweets() throws InterruptedException {
        return hoseBirdService.giveMeTweets();
    }
}
