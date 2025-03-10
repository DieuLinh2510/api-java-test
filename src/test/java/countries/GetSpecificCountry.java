package countries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static data.country.GetCountryData_New.GET_ALL_COUNTRIES;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GetSpecificCountry {
    @BeforeAll
    static void setUp_URI(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port=3000;

    }
    @Test
    void verifySpecificCountry(){
        Response response1=RestAssured.given().log().all().get("api/v1/countries/VN");
      // C2
        Response response=RestAssured.given().log().all().get("api/v1/countries/{code}","VN");
        response.then().statusCode(200);
        System.out.println(response.asString());
        String expected= """
    {"name":"Viet Nam","code":"VN"}
    """;
        assertThat(response.asString(),jsonEquals(expected));

    }
    static Stream<Map<String,String>> CountriesProvider() throws JsonProcessingException {
       ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> data=mapper.readValue(GET_ALL_COUNTRIES, new TypeReference<List<Map<String, String>>>() {
        });

     return data.stream();
    }
    // Cach nay dung chi khi data dang String - String neu dang String - Object se loi
    @ParameterizedTest
    @MethodSource("CountriesProvider")
    void verifySpecificCountryUsingParameterizedTest(Map<String,String> country){
        Response response1=RestAssured.given().log().all().get("api/v1/countries/VN");
        // C2: dung parameterizedTest
        Response response=RestAssured.given().log().all().get("api/v1/countries/{code}",country.get("code"));
        response.then().statusCode(200);
        System.out.println(response.asString());
        String expected= """
    {"name":"Viet Nam","code":"VN"}
    """;
        assertThat(response.asString(),jsonEquals(country));

    }
    // API get filter
    //>=, <= , > , <, ==, !=
    @Test
    void verifyCountriesGdp(){
        float gdp=5000;
        Response response=RestAssured.
                given().log().all()
                .queryParam("gdp",gdp)
                        .queryParam("operator",">=")
                .get("/api/v3/countries");
        //verify status code
        assertThat(response.statusCode(),equalTo(200));
        //Verify header

        //Verify body
        List<Map<String,String>> countries= response.as(new TypeRef<List<Map<String, String>>>() {

        });
        for (Map<String,String> country:countries){
            System.out.println(country);
            assertThat(Float.parseFloat(country.get("gdp")),greaterThan(gdp));
        }



    }
    static Stream<?> GDPOperatorProvider() throws JsonProcessingException {

        List<Map<String, String>> data=new ArrayList<>();
        data.add(Map.of("gdp","1906","operator",">="));
        data.add(Map.of("gdp","10000","operator",">="));


        return data.stream();
    }
    @ParameterizedTest
    @MethodSource("GDPOperatorProvider")
    void verifyCountriesParameterizedTest(Map<String,String> queryParams){
        Response response=RestAssured.
                given().log().all()
                .queryParams(queryParams)
                .get("/api/v3/countries");
        //verify status code
        assertThat(response.statusCode(),equalTo(200));
        //Verify header

        //Verify body
        List<Map<String,String>> countries= response.as(new TypeRef<List<Map<String, String>>>() {
        });
        for (Map<String,String> country:countries){
            System.out.println(country);
            assertThat(Float.parseFloat(country.get("gdp")),greaterThan(Float.parseFloat(queryParams.get("gdp"))));
        }


    }

}
