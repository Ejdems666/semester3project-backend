package api;

import com.nimbusds.jose.JOSEException;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;

/**
 * Created by adam on 29/11/2017.
 */
public class BookingTest extends FunctionalTest{
    @Test
    public void testSuccessfulBooking() throws JOSEException {
        String token = getAuthToken();

        Map<String, String> data = new HashMap<>();
        data.put("week", "2017-W44");
        given()
                .contentType("application/json")
                .body(data)
                .header("Authorization", "Bearer " + token)
                .when().post("/rentals/1/booking").then()
                .body("userName", hasItem("User"))
                .body("week", hasItem("2017-W44"));
    }

    @Test(priority = 1)
    public void testTakenBooking() throws JOSEException {
        String token = getAuthToken();

        Map<String, String> data = new HashMap<>();
        data.put("week", "2017-W44");
        given()
                .contentType("application/json")
                .body(data)
                .header("Authorization", "Bearer " + token)
                .when().post("/rentals/1/booking").then()
                .statusCode(400);
    }

    @Test(priority = 1)
    public void testGetAllBooking() throws JOSEException {
        given()
                .contentType("application/json")
                .when().get("/rentals/1/booking").then()
                .body("userName", hasItem("User"))
                .body("week", hasItem("2017-W44"));
    }
}