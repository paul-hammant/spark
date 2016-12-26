package spark.examples.templateview;

import spark.Service;

import java.util.HashMap;
import java.util.Map;

public class FreeMarkerExample {

    public static void main(String args[]) {

        new Service() {{

            get("/hello", (request, response) -> {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("message", "Hello FreeMarker World");

                // The hello.ftl file is located in directory:
                // src/test/resources/spark/examples/templateview/freemarker
                return modelAndView(attributes, "hello.ftl");
            }, new FreeMarkerTemplateEngine());

        }};

    }

}