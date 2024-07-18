import constants.Url;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static models.UserGenerator.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTests {

    User user;
    Response response;
    String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = Url.URL;
    }

    @Test
    @DisplayName("createNewUser")
    @Description("Успешное создание нового пользователя")
    public void createNewUser() {
        user = randomUser();

        response = sendPostAPIAuthRegister(user);

        checkResponseStatusCodeIs(response, 200);
        checkResponseKeyAndValueAre(response, "success", true);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createExistsUser")
    @Description("Невозможность создания существующего пользователя")
    public void createExistsUser() {
        user = randomUser();

        sendPostAPIAuthRegister(user);
        response = sendPostAPIAuthRegister(user);

        checkResponseStatusCodeIs(response, 403);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "User already exists");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createUserWithoutEmail")
    @Description("Невозможность создания пользователя без имейла")
    public void createUserWithoutEmail() {
        user = randomUserWithoutEmail();

        response = sendPostAPIAuthRegister(user);

        checkResponseStatusCodeIs(response, 403);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "Email, password and name are required fields");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createUserWithoutPassword")
    @Description("Невозможность создания пользователя без пароля")
    public void createUserWithoutPassword() {
        user = randomUserWithoutPassword();

        response = sendPostAPIAuthRegister(user);

        checkResponseStatusCodeIs(response, 403);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "Email, password and name are required fields");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createUserWithoutName")
    @Description("Невозможность создания пользователя без имени")
    public void createUserWithoutName() {
        user = randomUserWithoutName();

        response = sendPostAPIAuthRegister(user);

        checkResponseStatusCodeIs(response, 403);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "Email, password and name are required fields");
        response.body().prettyPeek();
    }

    @Step("Отправить POST запрос /api/auth/register")
    public Response sendPostAPIAuthRegister(User user) {
        return given().header("Content-type", "application/json; charset=utf-8").log().body().body(user).post("/api/auth/register");
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
        return token = response.then().extract().body().path("accessToken").toString().substring(7);
    }

    @Step("Отправить DELETE запрос /api/auth/user")
    public void sendDeleteAPIAuthUserByToken(String token) {
        this.token = token;
        given().header("Authorization", format("Bearer %s", token)).log().body().delete("api/auth/user");
    }


    @After
    public void tearDown() {
        if (response.statusCode() == 200) {
            sendDeleteAPIAuthUserByToken(token);
            System.out.println("Тестовый пользователь удален");
        }
    }
}

