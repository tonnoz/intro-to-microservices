package nl.sytac.intro.microservices.streamer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/tweet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TwitterEndpoint {

    @Autowired
    private HoseBirdService hoseBirdService;

    @GetMapping
    public ResponseEntity giveMeTweets(
                @RequestParam(value = "hashtag") String hashTag,
                @RequestParam(value = "max") Integer max,
                @RequestParam(value = "timeout") Integer timeout
            ) throws InterruptedException {

//        List<String> asd = Arrays.asList("{\"created_at\":\"Mon Apr 23 16:51:20 +0000 2018\",\"id\":988460321902915585}");
//        return asd;
        return new ResponseEntity<>(hoseBirdService.giveMeTweets(hashTag, max, timeout), HttpStatus.OK);
//        return hoseBirdService.giveMeTweets(hashTag, max, timeout);
    }
}
