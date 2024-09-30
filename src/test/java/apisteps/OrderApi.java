package apisteps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class OrderApi {

    public List<String> ingredients;

    public OrderApi(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Step("Создание заказа с ингредиентами.")
    public static OrderApi orderIngredients() {
        ValidatableResponse response = fetchIngredientsResponse();
        return new OrderApi(selectRandomIngredients(response));
    }

    @Step("Создание заказа без ингредиентов.")
    public static OrderApi orderWithoutIngredients() {
        return new OrderApi(new ArrayList<>());
    }

    @Step("Создание заказа с неверным хешем ингредиентов.")
    public static OrderApi orderWithIncorrectIngredients() {
        List<String> incorrectIngredients = new ArrayList<>();
        incorrectIngredients.add(RandomStringUtils.randomAlphabetic(24));
        return new OrderApi(incorrectIngredients);
    }
    @Step("Получение списка ингредиентов")
    public static ValidatableResponse fetchIngredientsResponse() {
        return given()
                .when()
                .get("https://stellarburgers.nomoreparties.site/api/ingredients")
                .then()
                .statusCode(200);
    }
    @Step("Выбор случайных ингредиентов.")
    private static List<String> selectRandomIngredients(ValidatableResponse response) {
        List<String> selectedIngredients = new ArrayList<>();
        List<String> availableIngredients = response.extract().path("data._id");

        // Выбираем случайное количество ингредиентов
        int numberOfIngredientsToSelect = nextInt(1, availableIngredients.size());

        for (int i = 0; i < numberOfIngredientsToSelect; i++) {
            // Выбираем случайный индекс из доступных ингредиентов
            int randomIndex = nextInt(0, availableIngredients.size());
            selectedIngredients.add(availableIngredients.get(randomIndex));
        }
        return selectedIngredients;
    }
}