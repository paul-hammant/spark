package spark.examples.transformer;


import spark.Service;

public class TransformerExample {

    public static void main(String args[]) {

        new Service() {{
            get("/hello", "application/json", (request, response) -> {
                return new MyMessage("Hello World");
            }, new JsonTransformer());

        }};
    }

}
