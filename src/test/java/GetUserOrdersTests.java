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

import static models.OrderGenerator.randomOrder;
import static models.UserGenerator.randomUser;

public class GetUserOrdersTests {

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
    @DisplayName("getOrder")
    @Description("Получение заказа конкретного пользователя")
    public void getOrder() {
        user = randomUser();
        order = randomOrder();
        userClient.sendPostAPIAuthRegister(user);
        token = userClient.getTokenFromResponse(userClient.sendPostRequestAPIAuthLogin(user));
        ordersClient.sendPostRequestAPIOrders(token, order);

        response = ordersClient.sendGETRequestAPIOrders(token);

        ordersClient.checkResponseStatusCodeIs(response, 200);
        ordersClient.checkResponseKeyAndValueAre(response, "success", true);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("getOrderWithoutAuthorization")
    @Description("Невозможность получения заказа без авторизации пользователя")
    public void getOrderWithoutAuthorization() {
        user = randomUser();
        order = randomOrder();
        token = userClient.getTokenFromResponse(userClient.sendPostAPIAuthRegister(user));
        ordersClient.sendPostRequestAPIOrders(token, order);

        response = ordersClient.sendGETRequestAPIOrders(token);

        ordersClient.checkResponseStatusCodeIs(response, 401);
        ordersClient.checkResponseKeyAndValueAre(response, "success", false);
        ordersClient.checkResponseKeyAndValueAre(response, "message", "You should be authorised");
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
