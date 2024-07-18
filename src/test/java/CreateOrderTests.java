import constants.Url;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static models.OrderGenerator.*;
import static models.UserGenerator.randomUser;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTests {

    CreateUserTests createUserTests = new CreateUserTests();
    AuthorizationUserTests authorizationUserTests = new AuthorizationUserTests();
    UpdateUserTests updateUserTests = new UpdateUserTests();
    User user;
    Response response;
    String token;
    Order order;

    @Before
    public void setUp() {
        RestAssured.baseURI = Url.URL;
    }

    @Test
    @DisplayName("createNewOrder")
    @Description("Создание обычного заказа")
    public void createNewOrder() {
        user = randomUser();
        order = randomOrder();
        createUserTests.sendPostAPIAuthRegister(user);
        token = authorizationUserTests.getTokenFromResponse(authorizationUserTests.sendPostRequestAPIAuthLogin(user));

        response = sendPostRequestAPIOrders(token, order);

        checkResponseStatusCodeIs(response, 200);
        checkResponseKeyAndValueAre(response, "success", true);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createNewOrderForNotAuthorizedUser")
    @Description("Невозможность создания заказа без авторизации пользователя")
    public void createNewOrderForNotAuthorizedUser() {
        user = randomUser();
        order = randomOrder();
        token = authorizationUserTests.getTokenFromResponse(createUserTests.sendPostAPIAuthRegister(user));

        response = sendPostRequestAPIOrders(token, order);

        checkResponseStatusCodeIs(response, 401);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "You should be authorised");
    }

    @Test
    @DisplayName("createNewOrderWithoutIngredients")
    @Description("Невозможность создания заказа без ингридиентов")
    public void createNewOrderWithoutIngredients() {
        user = randomUser();
        order = randomOrderWithoutIngredients();
        createUserTests.sendPostAPIAuthRegister(user);
        token = authorizationUserTests.getTokenFromResponse(authorizationUserTests.sendPostRequestAPIAuthLogin(user));

        response = sendPostRequestAPIOrders(token, order);

        checkResponseStatusCodeIs(response, 400);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "Ingredient ids must be provided");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createNewOrderWithInvalidIngredientsHash")
    @Description("Невозможность создания заказа с невалидным хеш ингредиента")
    public void createNewOrderWithInvalidIngredientsHash() {
        user = randomUser();
        order = randomOrderWithInvalidIngredientsHash();
        createUserTests.sendPostAPIAuthRegister(user);
        token = authorizationUserTests.getTokenFromResponse(authorizationUserTests.sendPostRequestAPIAuthLogin(user));

        response = sendPostRequestAPIOrders(token, order);

        checkResponseStatusCodeIs(response, 500);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "Internal Server Error");
        response.body().prettyPeek();
    }

    @Step("Отправить POST запрос api/orders")
    public Response sendPostRequestAPIOrders(String token, Order order) {
        return given().header("Content-type", "application/json; charset=utf-8")
                .header("Authorization", format("Bearer %s", token))
                .log().body().body(order).post("api/orders");
    }

    @Step("Проверить статус-код ответа")
    public void checkResponseStatusCodeIs(Response response, int code) {
        response.then().assertThat().statusCode(code);
    }

    @Step("Проверить ключ и значение ответа")
    public void checkResponseKeyAndValueAre(Response response, String key, Object value) {
        response.then().assertThat().body(key, equalTo(value));
    }

    @After
    public void tearDown() {
        if (!Objects.equals(this.token, "")) {
            updateUserTests.sendDeleteAPIAuthUser(token);
            System.out.println("Тестовый пользователь удален");
        }
    }
}
