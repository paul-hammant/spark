package spark;

import java.io.IOException;

import org.junit.*;

import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class FilterTest {
    private SparkTestUtil testUtil;
    private Service svc;

    @After
    public void tearDown() {
        svc.stop();
    }

    @Before
    public void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        svc = new Service() {{

            before("/justfilter", (q, a) -> System.out.println("Filter matched"));
            awaitInitialization();

        }};
    }

    @Test
    public void testJustFilter() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/justfilter", null);

        System.out.println("response.status = " + response.status);
        Assert.assertEquals(404, response.status);
    }

}
