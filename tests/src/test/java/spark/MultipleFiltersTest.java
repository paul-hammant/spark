package spark;

import org.junit.*;

import spark.util.SparkTestUtil;

/**
 * Basic test to ensure that multiple before and after filters can be mapped to a route.
 */
public class MultipleFiltersTest {
    
    private static SparkTestUtil http;
    private Service svc;


    @Before
    public void setup() {
        http = new SparkTestUtil(4567);

        svc = new Service() {{

            before("/user", initializeCounter, incrementCounter, loadUser);

            after("/user", incrementCounter, (req, res) -> {
                int counter = req.attribute("counter");
                Assert.assertEquals(counter, 2);
            });

            get("/user", (request, response) -> {
                Assert.assertEquals((int) request.attribute("counter"), 1);
                return ((User) request.attribute("user")).name();
            });

            awaitInitialization();

        }};
    }

    @After
    public void stopServer() {
        svc.stop();
    }

    @Test
    public void testMultipleFilters() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/user");
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Kevin", response.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Filter loadUser = (request, response) -> {
        User u = new User();
        u.name("Kevin");
        request.attribute("user", u);
    };

    private static Filter initializeCounter = (request, response) -> request.attribute("counter", 0);

    private static Filter incrementCounter = (request, response) -> {
        int counter = request.attribute("counter");
        counter++;
        request.attribute("counter", counter);
    };

    private static class User {

        private String name;

        public String name() {
            return name;
        }

        public void name(String name) {
            this.name = name;
        }
    }
}
