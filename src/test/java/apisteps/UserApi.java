package apisteps;

import pojo.UserData;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserApi {
    public static final String baseUrl = "https://stellarburgers.nomoreparties.site";
    private static final String USER_REGISTER_PATH = "api/auth/register";
    private static final String USER_DELETE_PATH = "api/auth/user";
    private static final String USER_LOGIN_PATH = "api/auth/login";
    private static final String USER_CHANGE_DATA_PATH = "api/auth/user";
    private static final String ORDER_PATH = "api/orders";

    UserData userData = new UserData();

    @Step("Регистрация пользователя.")
    public Response createUser(UserData userData) {
        return given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post(USER_REGISTER_PATH);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String UserAccessToken) {
        return given()
                .contentType("application/json")
                .header("Authorization", UserAccessToken)
                .when()
                .delete(USER_DELETE_PATH);
    }
    @Step("Авторизация пользователя.")
    public Response loginUser(UserData userData) {
        return given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post(USER_LOGIN_PATH);
    }

    @Step("Смена данных авторизованным пользователем.")
    public Response changeAuthorizedUserData(UserData userData, String UserAccessToken) {
        return given()
                .contentType("application/json")
                .body(userData)
                .header("Authorization", UserAccessToken)
                .when()
                .patch(USER_CHANGE_DATA_PATH);

    }

    @Step("Смена данных неавторизованным пользователем.")
    public Response changeUnauthorizedUserData(UserData userData) {
        return given()
                .contentType("application/json")
                .body(userData)
                .when()
                .patch(USER_CHANGE_DATA_PATH);

    }

    @Step("Создание заказа авторизованным пользователем.")
    public Response createOrderDataWithToken(OrderApi orderData, String UserAccessToken) {
        return given()
                .contentType("application/json")
                .body(orderData)
                .header("Authorization", UserAccessToken)
                .when()
                .post(ORDER_PATH);

    }

    @Step("Создание заказа неавторизованным пользователем.")
    public Response createOrderDataNotToken(OrderApi orderData) {
        return given()
                .contentType("application/json")
                .body(orderData)
                .when()
                .post(ORDER_PATH);

    }

    @Step("Получение заказов авторизованным пользователем.")
    public Response getUserOrderWithToken(String UserAccessToken) {
        return given()
                .contentType("application/json")
                .header("Authorization", UserAccessToken)
                .when()
                .get(ORDER_PATH);
    }

    @Step("Получение заказов неавторизованным пользователем.")
    public Response getUserOrderWithoutToken() {
        return given()
                .contentType("application/json")
                .when()
                .get(ORDER_PATH);
    }

}
