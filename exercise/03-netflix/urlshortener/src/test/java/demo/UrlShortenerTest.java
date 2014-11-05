package demo;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UrlShortener.class)
@WebAppConfiguration
@IntegrationTest({"server.port:0"})
public class UrlShortenerTest {
    @Value("${local.server.port}")
    int port;
    @Value("${urlshorten.url:http://localhost:${server.port:8080}}")
    String urlShortenUrl;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testSaveAndGet() throws Exception {
        given().log().all()
                .when()
                .urlEncodingEnabled(false)
                .post("/http://google.com")
                .then()
                .log().all()
                .statusCode(200)
                .body(is(urlShortenUrl + "/58f3ae21"));

        given().log().all()
                .when()
                .urlEncodingEnabled(false)
                .get("/58f3ae21")
                .then()
                .log().all()
                .statusCode(200)
                .body(is("http://google.com"));
    }

    @Test
    public void testInvalidUrl() throws Exception {
        given().log().all()
                .when()
                .urlEncodingEnabled(false)
                .post("/hoge")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    public void testNotExistHash() throws Exception {
        given().log().all()
                .when()
                .urlEncodingEnabled(false)
                .get("/hoge")
                .then()
                .log().all()
                .statusCode(404);
    }
}