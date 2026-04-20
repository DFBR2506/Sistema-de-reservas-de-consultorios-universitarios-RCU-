package co.edu.unimagdalena.RCU.exceptions;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}