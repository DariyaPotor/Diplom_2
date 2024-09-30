import POJO.UserData;
import apisteps.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class NewUserApiTests {

    private final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private UserApi userApi;
    public String newUserEmail = "dddddeddaas@yandex.ru";
    public String emailNotPassword = "iamgdoxgfdetteggfod@yandex.ru";
    public String newUserName = "Magomed";
    public String newUserPassword = "abobafederal";
    public String userAccessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        userApi = new UserApi();
    }
    @After
    public void tearDown(){
        UserData userData = new UserData(newUserEmail, newUserPassword,null);
        Response response = userApi.loginUser(userData);
        userAccessToken = response.then().extract().path("accessToken");

        if (userAccessToken != null && !userAccessToken.isEmpty()) {
            userApi.deleteUser(userAccessToken);
        }
    }

    @Test
    @Description("Создание уникального пользователя")
    public void createUniqueUser() {
        UserData userData = new UserData(newUserEmail, newUserPassword, newUserName);
        userApi.createUser(userData)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success",equalTo(true))
                .body("accessToken",notNullValue())
                .body("user.email",equalTo(newUserEmail))
                .body("user.name",equalTo(newUserName));
    }
    @Test
    @Description("Cоздание пользователя, который уже зарегистрирован")
    public void createRepeatUser() {
        UserData userData = new UserData(newUserEmail, newUserPassword, newUserName);
        userApi.createUser(userData);
        userApi.createUser(userData)
                .then()
                .assertThat()
                .statusCode(403)
                .body("success",equalTo(false))
                .body("message",equalTo("User already exists"));
    }
    @Test
    @Description("Cоздание пользователя, который который не указал email")
    public void createUserWithoutEmail() {
        UserData userData = new UserData(null, newUserPassword, newUserName);
        userApi.createUser(userData)
                .then()
                .assertThat()
                .statusCode(403)
                .body("success",equalTo(false))
                .body("message",equalTo("Email, password and name are required fields"));
    }
    @Test
    @Description("Cоздание пользователя, который который не указал password")
    public void createUserWithoutPassword() {
        UserData userData = new UserData(emailNotPassword, null, newUserName);
        userApi.createUser(userData)
                .then()
                .assertThat()
                .statusCode(403)
                .body("success",equalTo(false))
                .body("message",equalTo("Email, password and name are required fields"));
    }
    @Test
    @Description("Cоздание пользователя, который который не указал name")
    public void createUserWithoutName() {
        UserData userData = new UserData(emailNotPassword, newUserEmail, null);
        userApi.createUser(userData)
                .then()
                .assertThat()
                .statusCode(403)
                .body("success",equalTo(false))
                .body("message",equalTo("Email, password and name are required fields"));
    }
}
