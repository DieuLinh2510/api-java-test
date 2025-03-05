package countries;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.javacrumbs.jsonunit.core.Option;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

//0. Verify schema
//1. Verify status code
//2. Verify header
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
    void verifyGetCountryCorrectData(){
        String expectedResponse= """
                [{"name":"Viet Nam","code":"VN"},{"name":"USA","code":"US"},{"name":"Canada","code":"CA"},{"name":"UK","code":"GB"},{"name":"France","code":"FR"},{"name":"Japan","code":"JP"},{"name":"India","code":"IN"},{"name":"China","code":"CN"},{"name":"Brazil","code":"BR"}]
                > Task :test
                """;
        String expectedResponseOrder= """
                [{"name":"USA","code":"US"},{"name":"Viet Nam","code":"VN"},{"name":"Canada","code":"CA"},{"name":"UK","code":"GB"},{"name":"France","code":"FR"},{"name":"Japan","code":"JP"},{"name":"India","code":"IN"},{"name":"China","code":"CN"},{"name":"Brazil","code":"BR"}]
                > Task :test
                """;
        String expectedResponseFail="""
                [{"name":"Canada","code":"CA"},{"name":"UK","code":"GB"},{"name":"France","code":"FR"},{"name":"Japan","code":"JP"},{"name":"India","code":"IN"},{"name":"China","code":"CN"},{"name":"Brazil","code":"BR"}]
                > Task :test
                """;
        Response response=  RestAssured.get("/api/v1/countries");
        //0. Verify status code
        assertThat(response.statusCode(), equalTo(200));
        //1. Verify header
        assertThat(response.header("Content-Type"),equalTo("application/json; charset=utf-8"));
        assertThat(response.header("Content-type"),containsString("application/json"));
        //2. Verify body
        System.out.println(response.asString());
        assertThatJson(response.asString()).isEqualTo(expectedResponse);
        //WrongOrder
        assertThatJson(response.asString()).when(Option.IGNORING_ARRAY_ORDER).isEqualTo(expectedResponseOrder);
assertThatJson(response.asString()).when(Option.IGNORING_ARRAY_ORDER,Option.IGNORING_EXTRA_ARRAY_ITEMS).isEqualTo(expectedResponseFail);
    }
//    @Test
//    void getCountries_v2(){
//        get("/api/v1/countries/CA")
//                .then().log().all()
//                .statusCode(200);
//    }
//    @Test
//    void verifyGetCountrySchema(){
//        RestAssured.get("/api/v1/countries")
//                .then().log().all()
//                .statusCode(200).assertThat().body(matchesJsonSchemaInClasspath("da-get-country/get-country-json-schema.json"));
//           }
}
