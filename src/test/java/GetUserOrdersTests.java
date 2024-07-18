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
import static models.OrderGenerator.randomOrder;
import static models.UserGenerator.randomUser;
import static org.hamcrest.Matchers.equalTo;

public class GetUserOrdersTests {

    CreateUserTests createUserTests = new CreateUserTests();
    AuthorizationUserTests authorizationUserTests = new AuthorizationUserTests();
    UpdateUserTests updateUserTests = new UpdateUserTests();
    CreateOrderTests createOrderTests = new CreateOrderTests();
    User user;
    Response response;
    String token;
    Order order;

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
        createUserTests.sendPostAPIAuthRegister(user);
        token = authorizationUserTests.getTokenFromResponse(authorizationUserTests.sendPostRequestAPIAuthLogin(user));
        createOrderTests.sendPostRequestAPIOrders(token, order);

        response = sendGETRequestAPIOrders(token);

        checkResponseStatusCodeIs(response, 200);
        checkResponseKeyAndValueAre(response, "success", true);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("getOrderWithoutAuthorization")
    @Description("Невозможность получения заказа без авторизации пользователя")
    public void getOrderWithoutAuthorization() {
        user = randomUser();
        order = randomOrder();
        token = authorizationUserTests.getTokenFromResponse(createUserTests.sendPostAPIAuthRegister(user));
        createOrderTests.sendPostRequestAPIOrders(token, order);

        response = sendGETRequestAPIOrders(token);

        checkResponseStatusCodeIs(response, 401);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "You should be authorised");
        response.body().prettyPeek();
    }

    @Step("Отправить GET запрос api/orders")
    public Response sendGETRequestAPIOrders(String token) {
        return given().header("Content-type", "application/json; charset=utf-8")
                .header("Authorization", format("Bearer %s", token))
                .log().body().get("api/orders");
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
