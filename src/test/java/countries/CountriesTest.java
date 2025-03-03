package countries;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;

public class CountriesTest {
    @BeforeAll
    static void setUp_URI(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port=3000;

    }
    @Test
    void getCountries(){
        get("/api/v1/countries")
                .then().log().all()
                .statusCode(200);
    }
    @Test
    void getCountries_v2(){
        get("/api/v1/countries/CA")
                .then().log().all()
                .statusCode(200);
    }
}
