package utils;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class Utils {
    Faker faker = new Faker((new Locale("ru")));
    public String email = faker.internet().emailAddress();
    public String password = faker.funnyName().name();
    public String name = faker.name().firstName();
    //public String login = String.valueOf(faker.number().numberBetween(79000000000L, 79999999999L));
    //public String lastName = faker.name().lastName();
    //public String phoneNumber = login;
    //public String address = faker.address().streetAddress();
    //public String comment = faker.chuckNorris().fact();

    public String deliveryDay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
        return LocalDate.now().plusDays(1).format(formatter);
    }

}
