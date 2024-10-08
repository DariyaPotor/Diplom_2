import pojo.UserData;
import apisteps.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserDataApiTests {

    private UserApi userApi;
    public String userEmail = "dddddeddaas@yandex.ru";
    public String userName = "Magomed";
    public String userPassword = "abobafederal";
    public String userAccessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = UserApi.BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        userApi = new UserApi();
        UserData userData = new UserData(userEmail, userPassword, userName);
        userApi.createUser(userData);
        UserData userDataLogin = new UserData(userEmail, userPassword, null);
        Response response = userApi.loginUser(userDataLogin);
        userAccessToken = response.then().extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (userAccessToken != null && !userAccessToken.isEmpty()) {
            userApi.deleteUser(userAccessToken);
        }
    }

    @Test
    @Description("Изменение email авторизованного пользователя")
    public void changeAuthorizedUserEmail() {
        String newUserEmail = "meouuuuuuuuuuu@mail.ru";
        UserData userData = new UserData(newUserEmail, userPassword, userName);
        userApi.changeAuthorizedUserData(userData, userAccessToken)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newUserEmail))
                .body("user.name", equalTo(userName));
    }

    @Test
    @Description("Изменение password авторизованного пользователя")
    public void changeAuthorizedUserPassword() {
        String newUserPassword = "mainkun";
        UserData userData = new UserData(userEmail, newUserPassword, userName);
        userApi.changeAuthorizedUserData(userData, userAccessToken)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(userEmail))
                .body("user.name", equalTo(userName));
    }

    @Test
    @Description("Изменение name авторизованного пользователя")
    public void changeAuthorizedUserName() {
        String newUserName = "Garfild";
        UserData userData = new UserData(userEmail, userPassword, newUserName);
        userApi.changeAuthorizedUserData(userData, userAccessToken)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(userEmail))
                .body("user.name", equalTo(newUserName));
    }

    @Test
    @Description("Изменение данных неавторизованного пользователя")
    public void changeDataUnauthorizedUser() {
        String newUserName = "Garfild";
        UserData userData = new UserData(userEmail, userPassword, newUserName);
        userApi.changeUnauthorizedUserData(userData)
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @Description("Изменение данных авторизованного пользователя. Используется уже существующий email")
    public void changeUserDataSuchExistsEmail() {
        String newUserEmail = "meou@mail.ru";
        String newUserName = "Garfild";
        String newUserPassword = "mainkun";

        UserData newUserData = new UserData(newUserEmail, newUserPassword, newUserName);
        userApi.createUser(newUserData);

        UserData userData = new UserData(newUserEmail, userPassword, userName);
        userApi.changeAuthorizedUserData(userData, userAccessToken)
                .then()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));

        Response response = userApi.loginUser(newUserData);
        String newUserAccessToken = response.then().extract().path("accessToken");
        if (newUserAccessToken != null && !newUserAccessToken.isEmpty()) {
            userApi.deleteUser(newUserAccessToken);
        }

    }

}
