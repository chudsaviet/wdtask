package by.homeunix.wdtask.receiver;

import java.util.concurrent.atomic.AtomicLong;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
public class ReceiverController {

    static final ObjectMapper mapper = new ObjectMapper();
    static final AtomicLong messages_received = new AtomicLong(0);

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
    ResponseEntity receive(@RequestBody String payload) throws IOException{
        if (!isValidJSON(payload)) {
            throw new IllegalArgumentException();
        }
        long received_fact = messages_received.incrementAndGet();
        return new ResponseEntity(String.format("Received message number %d", received_fact), HttpStatus.OK);
    }

    @GetMapping(name="/service_stats")
    ResponseEntity serviceStats() {
        ObjectNode json = mapper.createObjectNode();
        json.put("messages_received", messages_received.get());
        return new ResponseEntity(json.toString(), HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity handleIllegalArgumentException() {
        return new ResponseEntity("Cannot parse JSON body", HttpStatus.BAD_REQUEST);
    }
}
