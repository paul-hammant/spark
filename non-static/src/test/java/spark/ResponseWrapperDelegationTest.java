package spark;

import org.junit.*;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import java.io.IOException;


public class ResponseWrapperDelegationTest {

    SparkTestUtil testUtil;
    private Service svc;

    @After
    public void tearDown() {
        svc.stop();
    }

    @Before
    public void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        svc = new Service() {{

        get("/204", (q, a) -> {
            a.status(204);
            return "";
        });

        after("/204", (q, a) -> {
            if (a.status() == 204) {
                a.status(200);
                a.body("ok");
            }
        });

        get("/json", (q, a) -> {
            a.type("application/json");
            return "{\"status\": \"ok\"}";
        });

        after("/json", (q, a) -> {
            if ("application/json".equalsIgnoreCase(a.type())) {
                a.type("text/plain");
            }
        });

        exception(Exception.class, (exception, q, a) -> {
            exception.printStackTrace();
        });

        awaitInitialization();

        }};
    }

    @Test
    public void filters_can_detect_response_status() throws Exception {
        UrlResponse response = testUtil.get("/204");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("ok", response.body);
    }

    @Test
    public void filters_can_detect_content_type() throws Exception {
        UrlResponse response = testUtil.get("/json");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("{\"status\": \"ok\"}", response.body);
        Assert.assertEquals("text/plain", response.headers.get("Content-Type"));
    }
}
