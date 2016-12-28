package spark.examples.accept;

import spark.Service;

public class JsonAcceptTypeExample {

    public static void main(String args[]) {

        new Service() {{

            //Running curl -i -H "Accept: application/json" http://localhost:4567/hello json message is read.
            //Running curl -i -H "Accept: text/html" http://localhost:4567/hello HTTP 404 error is thrown.
            get("/hello", "application/json", (request, response) -> {
                return "{\"message\": \"Hello World\"}";
            });

        }};

    }

}
