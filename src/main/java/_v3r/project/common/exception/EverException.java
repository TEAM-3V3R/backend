package _v3r.project.common.exception;

import _v3r.project.common.apiResponse.ErrorCode;
import lombok.Getter;

@Getter
public class EverException extends RuntimeException {
    private final ErrorCode errorCode;

    public EverException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public int getHttpStatusCode() {
        return this.errorCode.getStatusCode();
    }

}
