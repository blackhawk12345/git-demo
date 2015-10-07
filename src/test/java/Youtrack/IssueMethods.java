package Youtrack;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class IssueMethods {
    Cookies cookies;

    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI = "http://gorest.myjetbrains.com/youtrack/rest/";

        Response response =
                given().
                param("login", "hawk_2001@ukr.net").
                param("password", "drone123").
        when().
                post("/user/login");

                        cookies = response.getDetailedCookies();
    }

    @Test
    public void testCreateIssue() throws Exception {
        given().
                cookies(cookies).
                param("project", "GAQA1").
                param("summary", "Some summary").
                param("description", "Some descr").
        when().
                put("/issue").
        then().
                statusCode(201);
    }
    private String createTestIssue() throws Exception {
        Response response =
                given().
                        cookies(cookies).
                        param("project", "GAQA1").
                        param("summary", "Test summary").
                        param("description", "Test description").
                when().
                        put("/issue");

        String location = response.getHeader("location");
        String issueId = location.substring(location.lastIndexOf("/") + 1);
        return issueId;
    }

    @Test
    public void testDeleteIssue() throws Exception {
        String issueID = createTestIssue();

        given().
                cookies(cookies).
                when().
                delete("/issue/" + issueID).
                then().
                statusCode(200);
                }

    @Test
    public void testGetIssue() throws Exception {
        String issueID = createTestIssue();

        Response response =
        given().
                cookies(cookies).
        when().
                get("/issue/" + issueID).
        then().
                statusCode(200).
                body("issue.@id", equalTo(issueID)).
        extract().response();

        System.out.print(response.asString());
    }

    @Test
    public void testIssueExists() throws Exception {
        String issueID = createTestIssue();

        given().
                cookies(cookies).
        when().
                get("/issue/" + issueID + "/exists").
        then().
                statusCode(200);
    }

    @Test
    public void testIssueNotExists() throws Exception {
        String issueID = "12345";

        given().
                cookies(cookies).
        when().
                get("/issue/" + issueID + "/exists").
        then().
                statusCode(200);
    }
}
