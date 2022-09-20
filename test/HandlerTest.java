import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HandlerTest {
    Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Handler();
    }

    @Test
    void getQueryParameters() throws UnsupportedEncodingException {
        Map expected = new HashMap();
        expected.put("municipality","Copenhagen");
        expected.put("date","2022-09-19");
        assertEquals(expected, handler.getQueryParameters("municipality=Copenhagen&date=2022-09-19"));
    }
}