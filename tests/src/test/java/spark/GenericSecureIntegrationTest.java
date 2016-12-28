package spark;

import org.junit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class GenericSecureIntegrationTest {

    private SparkTestUtil testUtil;

    private final Logger LOGGER = LoggerFactory.getLogger(GenericSecureIntegrationTest.class);
    private Service svc;

    @After
    public void tearDown() {
        svc.stop();
    }

    @Before
    public void setup() {
        testUtil = new SparkTestUtil(4567);

        svc = new Service() {
            {

                // note that the keystore stuff is retrieved from SparkTestUtil which
                // respects JVM params for keystore, password
                // but offers a default included store if not.
                secure(SparkTestUtil.getKeyStoreLocation(),
                        SparkTestUtil.getKeystorePassword(), null, null);

                before("/protected/*", (request, response) -> {
                    halt(401, "Go Away!");
                });

                get("/hi", (request, response) -> {
                    return "Hello World!";
                });

                get("/:param", (request, response) -> {
                    return "echo: " + request.params(":param");
                });

                get("/paramwithmaj/:paramWithMaj", (request, response) -> {
                    return "echo: " + request.params(":paramWithMaj");
                });

                get("/", (request, response) -> {
                    return "Hello Root!";
                });

                post("/poster", (request, response) -> {
                    String body = request.body();
                    response.status(201); // created
                    return "Body was: " + body;
                });

                patch("/patcher", (request, response) -> {
                    String body = request.body();
                    response.status(200);
                    return "Body was: " + body;
                });

                after("/hi", (request, response) -> {
                    response.header("after", "foobar");
                });

                awaitInitialization();

            }};
    }

    @Test
    public void testGetHi() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethodSecure("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }

    @Test
    public void testHiHead() throws Exception {
        UrlResponse response = testUtil.doMethodSecure("HEAD", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("", response.body);
    }

    @Test
    public void testGetHiAfterFilter() throws Exception {
        UrlResponse response = testUtil.doMethodSecure("GET", "/hi", null);
        Assert.assertTrue(response.headers.get("after").contains("foobar"));
    }

    @Test
    public void testGetRoot() throws Exception {
        UrlResponse response = testUtil.doMethodSecure("GET", "/", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello Root!", response.body);
    }

    @Test
    public void testEchoParam1() throws Exception {
        UrlResponse response = testUtil.doMethodSecure("GET", "/shizzy", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: shizzy", response.body);
    }

    @Test
    public void testEchoParam2() throws Exception {
        UrlResponse response = testUtil.doMethodSecure("GET", "/gunit", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: gunit", response.body);
    }

    @Test
    public void testEchoParamWithMaj() throws Exception {
        UrlResponse response = testUtil.doMethodSecure("GET", "/paramwithmaj/plop", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: plop", response.body);
    }

    @Test
    public void testUnauthorized() throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure("GET", "/protected/resource", null);
        Assert.assertTrue(urlResponse.status == 401);
    }

    @Test
    public void testNotFound() throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure("GET", "/no/resource", null);
        Assert.assertTrue(urlResponse.status == 404);
    }

    @Test
    public void testPost() throws Exception {
        UrlResponse response = testUtil.doMethodSecure("POST", "/poster", "Fo shizzy");
        LOGGER.info(response.body);
        Assert.assertEquals(201, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }

    @Test
    public void testPatch() throws Exception {
        UrlResponse response = testUtil.doMethodSecure("PATCH", "/patcher", "Fo shizzy");
        LOGGER.info(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }
}