import pojo.UserData;
import apisteps.OrderApi;
import apisteps.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static apisteps.OrderApi.orderIngredients;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetUserOrdersTests {

    private UserApi userApi;
    public String userEmail = "dddddeddaas@yandex.ru";
    public String userName = "Magomed";
    public String userPassword = "abobafederal";
    public String userAccessToken;

    @Before
    public void setUp() {
// регистрация пользователя
        RestAssured.baseURI = UserApi.BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        userApi = new UserApi();
        UserData userData = new UserData(userEmail, userPassword, userName);
        userApi.createUser(userData);
// авторизация пользователя
        UserData userDataLogin = new UserData(userEmail, userPassword, null);
        Response response = userApi.loginUser(userDataLogin);
        userAccessToken = response.then().extract().path("accessToken");
// создание заказа пользователя
        OrderApi orderData = orderIngredients();
        userApi.createOrderDataWithToken(orderData, userAccessToken);
    }

    @After
    public void tearDown() {
        if (userAccessToken != null && !userAccessToken.isEmpty()) {
            userApi.deleteUser(userAccessToken);
        }
    }

    @Test
    @Description("Получение заказов с авторизацией")
    public void getOrderWithToken() {
        userApi.getUserOrderWithToken(userAccessToken)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders.total", notNullValue());
    }

    @Test
    @Description("Получение заказов с авторизацией")
    public void getOrderWithoutToken() {
        userApi.getUserOrderWithoutToken()
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
