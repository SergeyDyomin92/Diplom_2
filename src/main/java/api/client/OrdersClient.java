package api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;

public class OrdersClient extends BaseClient {

    @Step("Отправить GET запрос api/orders")
    public Response sendGETRequestAPIOrders(String token) {
        return given(getRequestSpec())
                .header("Authorization", format("Bearer %s", token))
                .get("api/orders");
    }

    @Step("Отправить POST запрос api/orders")
    public Response sendPostRequestAPIOrders(String token, Order order) {
        return given(getRequestSpec())
                .header("Authorization", format("Bearer %s", token))
                .body(order).post("api/orders");
    }

    @Step("Проверить статус-код ответа")
    public void checkResponseStatusCodeIs(Response response, int code) {
        response.then().assertThat().statusCode(code);
    }

    @Step("Проверить ключ и значение ответа")
    public void checkResponseKeyAndValueAre(Response response, String key, Object value) {
        response.then().assertThat().body(key, equalTo(value));
    }

}
