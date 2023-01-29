package cn.RecipeAPI.Exceptions;

public class NoSuchReviewException extends Exception{
    public NoSuchReviewException(String message) {
        super(message);
    }

    public NoSuchReviewException() {
    }
}
