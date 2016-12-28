package spark;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.util.SparkTestUtil;

public class BodyAvailabilityTest {

    private final Logger LOGGER = LoggerFactory.getLogger(BodyAvailabilityTest.class);

    private final String BODY_CONTENT = "the body content";

    private SparkTestUtil testUtil;

    private String beforeBody = null;
    private String routeBody = null;
    private String afterBody = null;
    private Service svc;

    @After
    public void tearDown() {
        svc.stop();

        beforeBody = null;
        routeBody = null;
        afterBody = null;
    }

    @Before
    public void setup() {
        LOGGER.debug("setup()");

        testUtil = new SparkTestUtil(4567);

        beforeBody = null;
        routeBody = null;
        afterBody = null;

        svc = new Service() {{

            before("/hello", (req, res) -> {
                LOGGER.debug("before-req.body() = " + req.body());
                beforeBody = req.body();
            });

            post("/hello", (req, res) -> {
                LOGGER.debug("get-req.body() = " + req.body());
                routeBody = req.body();
                return req.body();
            });

            after("/hello", (req, res) -> {
                LOGGER.debug("after-before-req.body() = " + req.body());
                afterBody = req.body();
            });

            awaitInitialization();

        }};
    }

    @Test
    public void testPost() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("POST", "/hello", BODY_CONTENT);
        LOGGER.info(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains(BODY_CONTENT));

        Assert.assertEquals(BODY_CONTENT, beforeBody);
        Assert.assertEquals(BODY_CONTENT, routeBody);
        Assert.assertEquals(BODY_CONTENT, afterBody);
    }
}