import api.client.OrdersClient;
import api.client.UserClient;
import constants.Url;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static models.OrderGenerator.*;
import static models.UserGenerator.randomUser;

public class CreateOrderTests {

    private User user;
    private Response response;
    private String token;
    private Order order;

    UserClient userClient = new UserClient();
    OrdersClient ordersClient = new OrdersClient();
    UpdateUserTests updateUserTests = new UpdateUserTests();

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
        userClient.sendPostAPIAuthRegister(user);
        token = userClient.getTokenFromResponse(userClient.sendPostRequestAPIAuthLogin(user));

        response = ordersClient.sendPostRequestAPIOrders(token, order);

        ordersClient.checkResponseStatusCodeIs(response, 200);
        ordersClient.checkResponseKeyAndValueAre(response, "success", true);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createNewOrderForNotAuthorizedUser")
    @Description("Невозможность создания заказа без авторизации пользователя")
    public void createNewOrderForNotAuthorizedUser() {
        user = randomUser();
        order = randomOrder();
        token = userClient.getTokenFromResponse(userClient.sendPostAPIAuthRegister(user));

        response = ordersClient.sendPostRequestAPIOrders(token, order);

        ordersClient.checkResponseStatusCodeIs(response, 401);
        ordersClient.checkResponseKeyAndValueAre(response, "success", false);
        ordersClient.checkResponseKeyAndValueAre(response, "message", "You should be authorised");
    }

    @Test
    @DisplayName("createNewOrderWithoutIngredients")
    @Description("Невозможность создания заказа без ингридиентов")
    public void createNewOrderWithoutIngredients() {
        user = randomUser();
        order = randomOrderWithoutIngredients();
        userClient.sendPostAPIAuthRegister(user);
        token = userClient.getTokenFromResponse(userClient.sendPostRequestAPIAuthLogin(user));

        response = ordersClient.sendPostRequestAPIOrders(token, order);

        ordersClient.checkResponseStatusCodeIs(response, 400);
        ordersClient.checkResponseKeyAndValueAre(response, "success", false);
        ordersClient.checkResponseKeyAndValueAre(response, "message", "Ingredient ids must be provided");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createNewOrderWithInvalidIngredientsHash")
    @Description("Невозможность создания заказа с невалидным хеш ингредиента")
    public void createNewOrderWithInvalidIngredientsHash() {
        user = randomUser();
        order = randomOrderWithInvalidIngredientsHash();
        userClient.sendPostAPIAuthRegister(user);
        token = userClient.getTokenFromResponse(userClient.sendPostRequestAPIAuthLogin(user));

        response = ordersClient.sendPostRequestAPIOrders(token, order);

        ordersClient.checkResponseStatusCodeIs(response, 500);
        ordersClient.checkResponseKeyAndValueAre(response, "success", false);
        ordersClient.checkResponseKeyAndValueAre(response, "message", "Internal Server Error");
        response.body().prettyPeek();
    }

    @After
    public void tearDown() {
        if (!Objects.equals(this.token, "")) {
            updateUserTests.sendDeleteAPIAuthUser(token);
            System.out.println("Тестовый пользователь удален");
        }
    }
}
