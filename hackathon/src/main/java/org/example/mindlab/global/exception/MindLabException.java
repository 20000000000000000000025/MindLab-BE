package org.example.mindlab.global.exception;

import lombok.Getter;
import org.example.mindlab.global.exception.error.ErrorCodes;

@Getter
public class MindLabException extends RuntimeException{ // todo 이름 변경할것

    private final ErrorCodes errorCodes;

    public MindLabException(ErrorCodes errorCodes) {
        super(errorCodes.getMessage());
        this.errorCodes = errorCodes;
    }
}
