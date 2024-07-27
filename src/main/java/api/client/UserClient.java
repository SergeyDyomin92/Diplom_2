package api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;

public class UserClient extends BaseClient {


    @Step("Отправить POST запрос /api/auth/register")
    public Response sendPostAPIAuthRegister(User user) {
        return given(getRequestSpec())
                .body(user).post("/api/auth/register");
    }

    @Step("Отправить POST запрос api/auth/login")
    public Response sendPostRequestAPIAuthLogin(User user) {
        return given(getRequestSpec())
                .body(user).post("/api/auth/login");
    }

    @Step("Проверить статус-код ответа")
    public void checkResponseStatusCodeIs(Response response, int code) {
        response.then().assertThat().statusCode(code);
    }

    @Step("Проверить ключ и значение ответа")
    public void checkResponseKeyAndValueAre(Response response, String key, Object value) {
        response.then().assertThat().body(key, equalTo(value));
    }

    @Step("Сохранить токен")
    public String getTokenFromResponse(Response response) {
        return response.then().extract().body().path("accessToken").toString().substring(7);
    }

    @Step("Отправить DELETE запрос /api/auth/user")
    public void sendDeleteAPIAuthUserByToken(String token) {
        given().header("Authorization", format("Bearer %s", token)).log().body().delete("api/auth/user");
    }
}
