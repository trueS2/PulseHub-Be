package pulsehub.pulsehubbe.global.exception;

import pulsehub.pulsehubbe.global.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private final ErrorCode errorCode;

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public GlobalException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode;
    }
}
