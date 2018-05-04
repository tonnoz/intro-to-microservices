package nl.sytac.intro.microservices.streamer;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TweetsWrapper {

    @JsonRawValue
    private List<String> tweets;
}
