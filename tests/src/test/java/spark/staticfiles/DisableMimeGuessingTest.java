/*
 * Copyright 2015 - Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.staticfiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Service;
import spark.examples.exception.NotFoundException;
import spark.util.SparkTestUtil;


/**
 * Test static files
 */
public class DisableMimeGuessingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFilesTest.class);

    private final String FO_SHIZZY = "Fo shizzy";
    private final String EXTERNAL_FILE_NAME_HTML = "externalFile.html";
    private final String CONTENT_OF_EXTERNAL_FILE = "Content of external file";

    private SparkTestUtil testUtil;

    private File tmpExternalFile;
    private Service svc;

    @After
    public void tearDown() {
        svc.stop();
        if (tmpExternalFile != null) {
            LOGGER.debug("tearDown().deleting: " + tmpExternalFile);
            tmpExternalFile.delete();
        }
    }

    @Before
    public void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        tmpExternalFile = new File(System.getProperty("java.io.tmpdir"), EXTERNAL_FILE_NAME_HTML);

        FileWriter writer = new FileWriter(tmpExternalFile);
        writer.write(CONTENT_OF_EXTERNAL_FILE);
        writer.flush();
        writer.close();

        svc = new Service() {{

            staticFiles.location("/public");
            staticFiles.externalLocation(System.getProperty("java.io.tmpdir"));
            staticFiles.disableMimeTypeGuessing();

            get("/hello", (q, a) -> FO_SHIZZY);

            get("/*", (q, a) -> {
                throw new NotFoundException();
            });

            init();


        }};
    }

    @Test
    public void testMimeTypes() throws Exception {
        svc.awaitInitialization();

        Assert.assertNull(doGet("/pages/index.html").headers.get("Content-Type"));
        Assert.assertNull(doGet("/js/scripts.js").headers.get("Content-Type"));
        Assert.assertNull(doGet("/css/style.css").headers.get("Content-Type"));
        Assert.assertNull(doGet("/img/sparklogo.png").headers.get("Content-Type"));
        Assert.assertNull(doGet("/img/sparklogo.svg").headers.get("Content-Type"));
        Assert.assertNull(doGet("/img/sparklogoPng").headers.get("Content-Type"));
        Assert.assertNull(doGet("/img/sparklogoSvg").headers.get("Content-Type"));
        Assert.assertNull(doGet("/externalFile.html").headers.get("Content-Type"));
    }

    @Test
    public void testCustomMimeType() throws Exception {
        svc.awaitInitialization();

        svc.staticFiles.registerMimeType("cxt", "custom-extension-type");
        Assert.assertNull(doGet("/img/file.cxt").headers.get("Content-Type"));
    }

    private SparkTestUtil.UrlResponse doGet(String fileName) throws Exception {
        return testUtil.doMethod("GET", fileName, null);
    }

}
