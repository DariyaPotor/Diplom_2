import apisteps.OrderApi;
import pojo.UserData;
import apisteps.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static apisteps.OrderApi.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTests {

    private UserApi userApi;
    public String userEmail = "dddddeddaas@yandex.ru";
    public String userName = "Magomed";
    public String userPassword = "abobafederal";
    public String userAccessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = UserApi.baseUrl;
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
    @Description("Создание заказа с авторизацией")
    public void createOrderWithAuth() {
        OrderApi orderData = orderIngredients();
        userApi.createOrderDataWithToken(orderData, userAccessToken)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        OrderApi orderData = orderIngredients();
        userApi.createOrderDataNotToken(orderData)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        OrderApi orderData = orderWithoutIngredients();
        userApi.createOrderDataWithToken(orderData, userAccessToken)
                .then()
                .assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void createOrderNotEligibleHash() {
        OrderApi orderData = orderWithIncorrectIngredients();
        userApi.createOrderDataWithToken(orderData, userAccessToken)
                .then()
                .assertThat()
                .statusCode(500);
    }
}
