package by.homeunix.wdtask.receiver;

import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReceiverController {

    static ObjectMapper mapper = new ObjectMapper();

    private static boolean isValidJSON(String json) throws IOException {
        boolean valid = true;
        try {
            mapper.readTree(json);
        } catch (JsonParseException e) {
            valid = false;
        }
        return valid;
    }

    @PostMapping(name="/receive", consumes={"application/json"})
    void receive(@RequestBody String payload) throws IOException{
        if (!isValidJSON(payload)) {
            throw new IllegalArgumentException();
        }
        System.out.println(payload);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgumentException() {
        return new ResponseEntity("Cannot parse JSON body", HttpStatus.BAD_REQUEST);
    }
}
