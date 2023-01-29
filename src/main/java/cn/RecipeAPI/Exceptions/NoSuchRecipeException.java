package cn.RecipeAPI.Exceptions;

public class NoSuchRecipeException extends Exception{
    public NoSuchRecipeException() {
    }

    public NoSuchRecipeException(String message) {
        super(message);
    }
}
