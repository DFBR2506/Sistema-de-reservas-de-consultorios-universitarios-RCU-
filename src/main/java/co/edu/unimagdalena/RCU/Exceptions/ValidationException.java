package co.edu.unimagdalena.RCU.exceptions;

import java.util.List;

import co.edu.unimagdalena.RCU.api.error.ApiError;
import co.edu.unimagdalena.RCU.api.error.ApiError.FieldViolation;

public class ValidationException extends BusinessException {
    
    private final List<FieldViolation> violations;
    
    
    public ValidationException(String message, List<FieldViolation> violations) {
        super(message);
        this.violations = violations;
    }

    public List<ApiError.FieldViolation> getViolations() {
        return violations;
    }
}
