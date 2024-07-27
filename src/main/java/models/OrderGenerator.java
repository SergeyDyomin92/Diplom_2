package models;

public class OrderGenerator {
    public static Order randomOrder() {
        return new Order()
                .withIngredients(new String[]{
                        "61c0c5a71d1f82001bdaaa74",
                        "61c0c5a71d1f82001bdaaa75",
                        "61c0c5a71d1f82001bdaaa70",
                        "61c0c5a71d1f82001bdaaa6d"});
    }

    public static Order randomOrderWithInvalidIngredientsHash() {
        return new Order()
                .withIngredients(new String[]{
                        "62c0c5a71d1f82001bdaaa74",
                        "62c0c5a71d1f82001bdaaa75",
                        "62c0c5a71d1f82001bdaaa70",
                        "62c0c5a71d1f82001bdaaa6d"});
    }

    public static Order randomOrderWithoutIngredients() {
        return new Order()
                .withIngredients(new String[]{});
    }
}
