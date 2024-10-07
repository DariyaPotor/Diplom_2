import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.*;
import apisteps.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserApiTests {

    private UserApi userApi;
    public String newUserEmail = "dddddeddaas@yandex.ru";
    public String incorrectUserEmail = "dddddedda@yandex.ru";
    public String newUserName = "Magomed";
    public String newUserPassword = "abobafederal";
    public String incorrectUserPassword = "abobafederall";
    public String userAccessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = UserApi.baseUrl;
        RestAssured.filters(new AllureRestAssured());
        userApi = new UserApi();
        UserData userData = new UserData(newUserEmail, newUserPassword, newUserName);
        userApi.createUser(userData);
    }

    @After
    public void tearDown() {
        UserData userData = new UserData(newUserEmail, newUserPassword, null);
        Response response = userApi.loginUser(userData);
        userAccessToken = response.then().extract().path("accessToken");
        if (userAccessToken != null && !userAccessToken.isEmpty()) {
            userApi.deleteUser(userAccessToken);
        }
    }

    @Test
    @Description("Логин под существующим пользователем")
    public void loginExistingUser() {
        UserData userData = new UserData(newUserEmail, newUserPassword, null);
        userApi.loginUser(userData)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("user.email", equalTo(newUserEmail))
                .body("user.name", equalTo(newUserName));
    }

    @Test
    @Description("Логин с неправильным паролем")
    public void loginIncorrectPassword() {
        UserData userData = new UserData(newUserEmail, incorrectUserPassword, null);
        userApi.loginUser(userData)
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @Description("Логин с неправильным email")
    public void loginIncorrectEmail() {
        UserData userData = new UserData(incorrectUserEmail, newUserPassword, null);
        userApi.loginUser(userData)
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}

