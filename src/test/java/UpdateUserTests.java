import api.client.UserClient;
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

public class UpdateUserTests {


    private User user;
    private Response response;
    private String token;

    Utils utils = new Utils();
    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = Url.URL;
    }

    @Test
    @DisplayName("updateAuthorizedUserName")
    @Description("Авторизованный пользователь. Обновление имени")
    public void updateAuthorizedUserName() {
        user = randomUser();
        userClient.sendPostAPIAuthRegister(user);
        Response authResponse = userClient.sendPostRequestAPIAuthLogin(user);
        String newName = utils.name;

        token = userClient.getTokenFromResponse(authResponse);
        response = sendPatchRequestAPIAuthUser(token, format("{\"name\": \"%s\"}", newName));

        checkResponseStatusCodeIs(response, 200);
        checkResponseKeyAndValueAre(response, "user.name", newName);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("updateAuthorizedUserEmail")
    @Description("Авторизованный пользователь. Обновление имейла")
    public void updateAuthorizedUserEmail() {
        user = randomUser();
        userClient.sendPostAPIAuthRegister(user);
        Response authResponse = userClient.sendPostRequestAPIAuthLogin(user);
        String newEmail = utils.email;

        token = userClient.getTokenFromResponse(authResponse);
        response = sendPatchRequestAPIAuthUser(token, format("{\"email\": \"%s\"}", newEmail));

        checkResponseStatusCodeIs(response, 200);
        checkResponseKeyAndValueAre(response, "user.email", newEmail);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("updateAuthorizedUserBusyEmail")
    @Description("Авторизованный пользователь. Невозможность обновления имейла на используемый")
    public void updateAuthorizedUserBusyEmail() {
        User preContitionUser = randomUser();
        userClient.sendPostAPIAuthRegister(preContitionUser);
        user = randomUser();
        userClient.sendPostAPIAuthRegister(user);
        Response authResponse = userClient.sendPostRequestAPIAuthLogin(user);

        token = userClient.getTokenFromResponse(authResponse);
        response = sendPatchRequestAPIAuthUser(token, format("{\"email\": \"%s\"}", preContitionUser.getEmail()));

        checkResponseStatusCodeIs(response, 403);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "User with such email already exists");
    }

    @Test
    @DisplayName("updateAuthorizedUserPassword")
    @Description("Авторизованный пользователь. Обновление пароля")
    public void updateAuthorizedUserPassword() {
        user = randomUser();
        userClient.sendPostAPIAuthRegister(user);
        Response authResponse = userClient.sendPostRequestAPIAuthLogin(user);
        String newPassword = utils.password;

        token = userClient.getTokenFromResponse(authResponse);
        response = sendPatchRequestAPIAuthUser(token, format("{\"password\": \"%s\"}", newPassword));

        checkResponseStatusCodeIs(response, 200);
        checkResponseKeyAndValueAre(response, "success", true);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("updateNotAuthorizedUserName")
    @Description("Неавторизованный пользователь. Невозможность обновления имени")
    public void updateNotAuthorizedUserName() {
        user = randomUser();
        token = userClient.getTokenFromResponse(userClient.sendPostAPIAuthRegister(user));
        response = sendPatchRequestAPIAuthUser(token, format("{\"name\": \"%s\"}", utils.name));

        checkResponseStatusCodeIs(response, 401);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "You should be authorised");
    }

    @Test
    @DisplayName("updateNotAuthorizedUserEmail")
    @Description("Неавторизованный пользователь. Невозможность обновления имейла")
    public void updateNotAuthorizedUserEmail() {
        user = randomUser();
        token = userClient.getTokenFromResponse(userClient.sendPostAPIAuthRegister(user));
        response = sendPatchRequestAPIAuthUser(token, format("{\"email\": \"%s\"}", utils.email));

        checkResponseStatusCodeIs(response, 401);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "You should be authorised");
    }

    @Test
    @DisplayName("updateNotAuthorizedUserBusyEmail")
    @Description("Неавторизованный пользователь. Невозможность обновления имейла на используемый")
    public void updateNotAuthorizedUserBusyEmail() {
        User preContitionUser = randomUser();
        userClient.sendPostAPIAuthRegister(preContitionUser);
        user = randomUser();
        token = userClient.getTokenFromResponse(userClient.sendPostAPIAuthRegister(user));
        response = sendPatchRequestAPIAuthUser(token, format("{\"email\": \"%s\"}", preContitionUser.getEmail()));

        checkResponseStatusCodeIs(response, 403);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "User with such email already exists");
    }

    @Test
    @DisplayName("updateNotAuthorizedUserPassword")
    @Description("Неавторизованный пользователь. Невозможность обновления пароля")
    public void updateNotAuthorizedUserPassword() {
        user = randomUser();
        token = userClient.getTokenFromResponse(userClient.sendPostAPIAuthRegister(user));
        response = sendPatchRequestAPIAuthUser(token, format("{\"password\": \"%s\"}", utils.password));

        checkResponseStatusCodeIs(response, 401);
        checkResponseKeyAndValueAre(response, "success", false);
        checkResponseKeyAndValueAre(response, "message", "You should be authorised");
    }

    @Step("Отправить PATCH запрос api/auth/user")
    public Response sendPatchRequestAPIAuthUser(String token, String body) {
        return given().header("Content-type", "application/json")
                .header("Authorization", format("Bearer %s", token))
                .log().body().body(body).patch("api/auth/user");
    }

    @Step("Проверить статус-код ответа")
    public void checkResponseStatusCodeIs(Response response, int code) {
        response.then().assertThat().statusCode(code);
    }

    @Step("Проверить ключ и значение ответа")
    public void checkResponseKeyAndValueAre(Response response, String key, Object value) {
        response.then().assertThat().body(key, equalTo(value));
    }

    @Step("Отправить DELETE запрос /api/auth/user")
    public void sendDeleteAPIAuthUser(String token) {
        this.token = token;
        given().header("Authorization", format("Bearer %s", token)).log().body().delete("api/auth/user");
    }

    @After
    public void tearDown() {
        if (!Objects.equals(this.token, "")) {
            sendDeleteAPIAuthUser(token);
            System.out.println("Тестовый пользователь удален");
        }
    }
}
