package spark.customerrorpages;

import java.io.IOException;

import org.junit.*;

import spark.CustomErrorPages;
import spark.Service;
import spark.util.SparkTestUtil;


public class CustomErrorPagesTest {

    private static final String CUSTOM_NOT_FOUND = "custom not found 404";
    private static final String CUSTOM_INTERNAL = "custom internal 500";
    private static final String HELLO_WORLD = "hello world!";
    public static final String APPLICATION_JSON = "application/json";
    private static final String QUERY_PARAM_KEY = "qparkey";

    SparkTestUtil testUtil;
    private  Service svc;

    @After
    public  void tearDown() {
        svc.stop();
    }

    @Before
    public void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        svc = new Service() {{

            get("/hello", (q, a) -> HELLO_WORLD);

            get("/raiseinternal", (q, a) -> {
                throw new Exception("");
            });

            notFound(CUSTOM_NOT_FOUND);

            internalServerError((request, response) -> {
                if (request.queryParams(QUERY_PARAM_KEY) != null) {
                    throw new Exception();
                }
                response.type(APPLICATION_JSON);
                return CUSTOM_INTERNAL;
            });

            awaitInitialization();

        }};
    }

    @Test
    public void testGetHi() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hello", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(HELLO_WORLD, response.body);
    }

    @Test
    public void testCustomNotFound() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/othernotmapped", null);
        Assert.assertEquals(404, response.status);
        Assert.assertEquals(CUSTOM_NOT_FOUND, response.body);
    }

    @Test
    public void testCustomInternal() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/raiseinternal", null);
        Assert.assertEquals(500, response.status);
        Assert.assertEquals(APPLICATION_JSON, response.headers.get("Content-Type"));
        Assert.assertEquals(CUSTOM_INTERNAL, response.body);
    }

    @Test
    public void testCustomInternalFailingRoute() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/raiseinternal?" + QUERY_PARAM_KEY + "=sumthin", null);
        Assert.assertEquals(500, response.status);
        Assert.assertEquals(CustomErrorPages.INTERNAL_ERROR, response.body);
    }
}
