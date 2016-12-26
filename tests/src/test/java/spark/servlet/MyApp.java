package spark.servlet;

import spark.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyApp implements SparkApplication {

    public static final String EXTERNAL_FILE = "externalFileServlet.html";

    static File tmpExternalFile;

    @Override
    public synchronized void init() {

        new Service() {{

            externalStaticFileLocation(System.getProperty("java.io.tmpdir"));
            staticFileLocation("/public");

            tmpExternalFile = new File(System.getProperty("java.io.tmpdir"), EXTERNAL_FILE);

            try {
                FileWriter writer = new FileWriter(tmpExternalFile);
                writer.write("Content of external file");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            before("/protected/*", (request, response) -> {
                halt(401, "Go Away!");
            });

            get("/hi", (request, response) -> {
                return "Hello World!";
            });

            get("/:param", (request, response) -> {
                return "echo: " + request.params(":param");
            });

            get("/", (request, response) -> {
                return "Hello Root!";
            });

            post("/poster", (request, response) -> {
                String body = request.body();
                response.status(201); // created
                return "Body was: " + body;
            });

            after("/hi", (request, response) -> {
                response.header("after", "foobar");
            });


        }};

    }

}
