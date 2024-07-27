package utils;

import com.github.javafaker.Faker;

import java.util.Locale;


public class Utils {
    Faker faker = new Faker((new Locale("ru")));
    public String email = faker.internet().emailAddress();
    public String password = faker.funnyName().name();
    public String name = faker.name().firstName();

}
