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
import utils.Utils;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static models.UserGenerator.randomUser;
import static org.hamcrest.Matchers.equalTo;

public class AuthorizationUserTests {

    CreateUserTests createUserTests = new CreateUserTests();
    Utils utils = new Utils();
    User user;
    Response response;
    String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = Url.URL;
    }

    @Test
    @DisplayName("authorizationUser")
    @Description("Успешная авторизация пользователя")
    public void authorizationUser() {
        user = randomUser();
        createUserTests.sendPostAPIAuthRegister(user);

        response = sendPostRequestAPIAuthLogin(user);

        checkResponseStatusCodeIs(response, 200);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("authorizationUserWithWrongEMail")
    @Description("Невозможность авторизации пользователя с невалидным имейлом")
    public void authorizationUserWithWrongEmail() {
        user = randomUser();
        createUserTests.sendPostAPIAuthRegister(user);

        response = sendPostRequestAPIAuthLogin(user.withEmail(utils.email).withPassword(user.getPassword()));

        checkResponseStatusCodeIs(response, 401);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "email or password are incorrect");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("authorizationUserWithWrongPassword")
    @Description("Невозможность авторизации пользователя с невалидным паролем")
    public void authorizationUserWithWrongPassword() {
        user = randomUser();
        createUserTests.sendPostAPIAuthRegister(user);

        response = sendPostRequestAPIAuthLogin(user.withEmail(user.getEmail()).withPassword(utils.password));

        checkResponseStatusCodeIs(response, 401);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "email or password are incorrect");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("authorizationUserWithWrongEmailAndPassword")
    @Description("Невозможность авторизации пользователя с невалидным имейлом и паролем")
    public void authorizationUserWithWrongEmailAndPassword() {
        user = randomUser();
        createUserTests.sendPostAPIAuthRegister(user);

        response = sendPostRequestAPIAuthLogin(user.withEmail(utils.email).withPassword(utils.password));

        checkResponseStatusCodeIs(response, 401);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "email or password are incorrect");
        response.body().prettyPeek();
    }

    @Step("Отправить POST запрос api/auth/login")
    public Response sendPostRequestAPIAuthLogin(User user) {
        return given().header("Content-type", "application/json; charset=utf-8").log().body().body(user).post("/api/auth/login");
    }

    @Step("Проверить статус-код ответа")
    public void checkResponseStatusCodeIs(Response response, int code) {
        response.then().assertThat().statusCode(code);
    }

    @Step("Проверить ключ и значение ответа")
    public void checkResponseKeyAndValueAre(Response response, String key, Object value) {
        response.then().assertThat().body(key, equalTo(value));
    }

    /**
     * Сохранение токена без префикса "Bearer " из ответа метода создания пользователя.
     */
    @Step("Сохранить токен")
    public String getTokenFromResponse(Response response) {
        return response.then().extract().body().path("accessToken").toString().substring(7);
    }

    @Step("Отправить DELETE запрос /api/auth/user")
    public void sendDeleteAPIAuthUserByToken(String token) {
        this.token = token;
        given().header("Authorization", format("Bearer %s", token)).log().body().delete("api/auth/user");
    }

    @After
    public void tearDown() {
        if (!Objects.equals(this.token, "")) {
            sendDeleteAPIAuthUserByToken(token);
            System.out.println("Тестовый пользователь удален");
        }
    }
}
