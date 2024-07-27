import api.client.UserClient;
import constants.Url;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.Utils;

import java.util.Objects;

import static models.UserGenerator.randomUser;

public class AuthorizationUserTests {

    private User user;
    private Response response;
    protected String token;

    Utils utils = new Utils();
    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = Url.URL;
    }

    @Test
    @DisplayName("authorizationUser")
    @Description("Успешная авторизация пользователя")
    public void authorizationUser() {
        user = randomUser();
        userClient.sendPostAPIAuthRegister(user);

        response = userClient.sendPostRequestAPIAuthLogin(user);

        userClient.checkResponseStatusCodeIs(response, 200);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("authorizationUserWithWrongEMail")
    @Description("Невозможность авторизации пользователя с невалидным имейлом")
    public void authorizationUserWithWrongEmail() {
        user = randomUser();
        userClient.sendPostAPIAuthRegister(user);

        response = userClient.sendPostRequestAPIAuthLogin(user.withEmail(utils.email).withPassword(user.getPassword()));

        userClient.checkResponseStatusCodeIs(response, 401);
        userClient.checkResponseKeyAndValueAre(response, "success", false);
        userClient.checkResponseKeyAndValueAre(response, "message", "email or password are incorrect");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("authorizationUserWithWrongPassword")
    @Description("Невозможность авторизации пользователя с невалидным паролем")
    public void authorizationUserWithWrongPassword() {
        user = randomUser();
        userClient.sendPostAPIAuthRegister(user);

        response = userClient.sendPostRequestAPIAuthLogin(user.withEmail(user.getEmail()).withPassword(utils.password));

        userClient.checkResponseStatusCodeIs(response, 401);
        userClient.checkResponseKeyAndValueAre(response, "success", false);
        userClient.checkResponseKeyAndValueAre(response, "message", "email or password are incorrect");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("authorizationUserWithWrongEmailAndPassword")
    @Description("Невозможность авторизации пользователя с невалидным имейлом и паролем")
    public void authorizationUserWithWrongEmailAndPassword() {
        user = randomUser();
        userClient.sendPostAPIAuthRegister(user);

        response = userClient.sendPostRequestAPIAuthLogin(user.withEmail(utils.email).withPassword(utils.password));

        userClient.checkResponseStatusCodeIs(response, 401);
        userClient.checkResponseKeyAndValueAre(response, "success", false);
        userClient.checkResponseKeyAndValueAre(response, "message", "email or password are incorrect");
        response.body().prettyPeek();
    }


    @After
    public void tearDown() {
        if (!Objects.equals(this.token, "")) {
            userClient.sendDeleteAPIAuthUserByToken(token);
            System.out.println("Тестовый пользователь удален");
        }
    }
}
