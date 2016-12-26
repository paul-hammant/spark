package spark.route;

import org.junit.*;

import spark.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static spark.route.RouteOverview.createHtmlForRouteTarget;
import static spark.route.RouteOverview.routes;

public class RouteOverviewTest {

    private Service svc;

    @Before
    public void setup() {
        routes = new ArrayList<>();
    }

    @After
    public void shutdown() throws Exception {
        if (svc != null) {
            svc.stop();
        }
    }

    @Test
    public void assertThat_noRoutesAreAddedByDefault() {
        assertThat(routes.size(), is(0));
    }

    @Test
    public void assertThat_unmappedRoute_doesNotWork() {
        Route unmappedRoute = (Request request, Response response) -> "";
        assertThat(createHtmlForRouteTarget(unmappedRoute), not(containsString("unmappedRoute")));
    }

    @Test
    public void assertThat_filter_field_works() {

        svc = new Service() {{
            port(0);
            before("/0", RouteOverviewTest.filterField);
        }};
        assertThat(routes.size(), is(1));
        assertEquals("[before, /0, <RouteOverviewTest.filterField>]", Arrays.deepToString(routes.toArray()));
    }

    @Test
    public void assertThat_filter_class_works() {

        svc = new Service() {{
            port(0);
            before("/1", new FilterImplementer());
        }};
        assertThat(routes.size(), is(1));
        assertEquals("[before, /1, <RouteOverviewTest.FilterImplementer>]", Arrays.deepToString(routes.toArray()));
    }

    @Test
    public void assertThat_filter_methodRef_works() {

        svc = new Service() {{
            port(0);
            FilterMethodRefClass.before(this);
        }};
        assertThat(routes.size(), is(1));
        assertThat(Arrays.deepToString(routes.toArray()), startsWith("[before, /2, spark.route.RouteOverviewTest$FilterMethodRefClass$$Lambda$"));

    }

    @Test
    public void assertThat_filter_lambda_works() {

        svc = new Service() {{
            port(0);
            FilterLambdaRefClass.before(this);

        }};
        assertThat(routes.size(), is(1));
        assertThat(Arrays.deepToString(routes.toArray()), startsWith("[before, 3, spark.route.RouteOverviewTest$FilterLambdaRefClass$$Lambda$"));
    }

    @Test
    public void assertThat_route_field_works() {

        svc = new Service() {{
            port(0);
            get("/4", RouteOverviewTest.routeField);
            awaitInitialization();
        }};
        assertThat(routes.size(), is(1));
        assertEquals("[get, /4, <RouteOverviewTest.routeField>]", Arrays.deepToString(routes.toArray()));

    }

    @Test
    public void assertThat_route_class_works() {
        svc = new Service() {{
            port(0);
            get("/5", new RouteImplementer());
        }};
        assertThat(routes.size(), is(1));
        assertEquals("[get, /5, <RouteOverviewTest.RouteImplementer>]", Arrays.deepToString(routes.toArray()));
    }

    @Test
    public void assertThat_route_methodRef_works() {
        svc = new Service() {{
            port(0);
            RouteMethodRefClass.get(this);
            awaitInitialization();
        }};
        assertThat(routes.size(), is(1));
        assertThat(Arrays.deepToString(routes.toArray()), startsWith("[get, /6, spark.route.RouteOverviewTest$RouteMethodRefClass$$Lambda$"));
    }

    @Test
    public void assertThat_route_lambda_works() {

        svc = new Service() {{
            port(0);
            get("/7", ((request, response) -> ""));
            awaitInitialization();
        }};
        assertThat(routes.size(), is(1));
        assertThat(Arrays.deepToString(routes.toArray()), startsWith("[get, /7, spark.route.RouteOverviewTest$8$$Lambda$"));

    }

    // fields/classes/methods to obtain names from
    private static Route routeField = new Route() {
        @Override
        public Object handle(Request request, Response response) throws Exception {
            return null;
        }
        @Override
        public String toString() {
            return "<RouteOverviewTest.routeField>";
        }
    };

    private static class RouteImplementer implements Route {
        @Override
        public Object handle(Request request, Response response) throws Exception {
            return "";
        }
        @Override
        public String toString() {
            return "<RouteOverviewTest.RouteImplementer>";
        }
    }
    public static class RouteMethodRefClass {

        public static void get(Service svc) {
            svc.get("/6", RouteMethodRefClass::routeMethodRef);
        }

        private static String routeMethodRef(Request request, Response response) {
            return "";
        }

    }

    private static Filter filterField = new Filter() {
        @Override
        public void handle(Request request, Response response) throws Exception {

        }

        @Override
        public String toString() {
            return "<RouteOverviewTest.filterField>";
        }
    };

    private static class FilterImplementer implements Filter {
        @Override
        public void handle(Request request, Response response) throws Exception {
        }
        @Override
        public String toString() {
            return "<RouteOverviewTest.FilterImplementer>";
        }

    }

    public static class FilterMethodRefClass {

        public static void before(Service svc) {
            svc.before("/2", FilterMethodRefClass::filterMethodRef);
        }

        private static void filterMethodRef(Request request, Response response) {
        }

    }

    public static class FilterLambdaRefClass {

        public static void before(Service svc) {
            svc.before("3", ((request, response) -> {
            }));
        }

    }

}
