package models;

public class Order {

    private String[] ingredients;

    public Order withIngredients(String[] ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }
}
