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

import static models.UserGenerator.*;

public class CreateUserTests {

    private User user;
    public Response response;
    protected String token;
    UserClient userClient = new UserClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = Url.URL;
    }

    @Test
    @DisplayName("createNewUser")
    @Description("Успешное создание нового пользователя")
    public void createNewUser() {
        user = randomUser();

        response = userClient.sendPostAPIAuthRegister(user);

        userClient.checkResponseStatusCodeIs(response, 200);
        userClient.checkResponseKeyAndValueAre(response, "success", true);
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createExistsUser")
    @Description("Невозможность создания существующего пользователя")
    public void createExistsUser() {
        user = randomUser();

        userClient.sendPostAPIAuthRegister(user);
        response = userClient.sendPostAPIAuthRegister(user);

        userClient.checkResponseStatusCodeIs(response, 403);
        userClient.checkResponseKeyAndValueAre(response, "success", false);
        userClient.checkResponseKeyAndValueAre(response, "message", "User already exists");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createUserWithoutEmail")
    @Description("Невозможность создания пользователя без имейла")
    public void createUserWithoutEmail() {
        user = randomUserWithoutEmail();

        response = userClient.sendPostAPIAuthRegister(user);

        userClient.checkResponseStatusCodeIs(response, 403);
        userClient.checkResponseKeyAndValueAre(response, "success", false);
        userClient.checkResponseKeyAndValueAre(response, "message", "Email, password and name are required fields");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createUserWithoutPassword")
    @Description("Невозможность создания пользователя без пароля")
    public void createUserWithoutPassword() {
        user = randomUserWithoutPassword();

        response = userClient.sendPostAPIAuthRegister(user);

        userClient.checkResponseStatusCodeIs(response, 403);
        userClient.checkResponseKeyAndValueAre(response, "success", false);
        userClient.checkResponseKeyAndValueAre(response, "message", "Email, password and name are required fields");
        response.body().prettyPeek();
    }

    @Test
    @DisplayName("createUserWithoutName")
    @Description("Невозможность создания пользователя без имени")
    public void createUserWithoutName() {
        user = randomUserWithoutName();

        response = userClient.sendPostAPIAuthRegister(user);

        userClient.checkResponseStatusCodeIs(response, 403);
        userClient.checkResponseKeyAndValueAre(response, "success", false);
        userClient.checkResponseKeyAndValueAre(response, "message", "Email, password and name are required fields");
        response.body().prettyPeek();
    }

    @After
    public void tearDown() {
        if (response.statusCode() == 200) {
            userClient.sendDeleteAPIAuthUserByToken(token);
            System.out.println("Тестовый пользователь удален");
        }
    }
}

