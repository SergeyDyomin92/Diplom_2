package models;

import utils.Utils;

public class UserGenerator {

    public static User randomUser() {
        Utils utils = new Utils();
        return new User()
                .withEmail(utils.email)
                .withPassword(utils.password)
                .withName(utils.name);
    }

    public static User randomUserWithoutEmail() {
        Utils utils = new Utils();
        return new User()
                .withPassword(utils.password)
                .withName(utils.name);
    }

    public static User randomUserWithoutPassword() {
        Utils utils = new Utils();
        return new User()
                .withEmail(utils.email)
                .withName(utils.name);
    }

    public static User randomUserWithoutName() {
        Utils utils = new Utils();
        return new User()
                .withEmail(utils.email)
                .withPassword(utils.password);
    }
}
