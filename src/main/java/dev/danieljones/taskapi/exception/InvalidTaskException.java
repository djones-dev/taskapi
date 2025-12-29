package dev.danieljones.taskapi.exception;

public class InvalidTaskException extends RuntimeException {
    
    public InvalidTaskException(String message) {
        super(message);
    }
    
    public InvalidTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}