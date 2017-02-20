package ru.mzaynutdinov.notes.utils;

/**
 * Исключения API
 */
public class ApiException extends RuntimeException {
    private final Type type;

    public Type getType() {
        return type;
    }

    public enum Type {
        NOTE_NOT_FOUND(1000, 404),
        DIFFERENT_NOTES_IDS(1001, 400),
        INCORRECT_START_DATE_FORMAT(1002, 400),
        INCORRECT_END_DATE_FORMAT(1003, 400),
        NOTES_NOT_FOUND(1004, 404);

        public final int code;
        public final int httpStatus;

        Type(int code, int httpStatus) {
            this.code = code;
            this.httpStatus = httpStatus;
        }
    }

    public ApiException(Type type) {
        super(type.name());
        this.type = type;
    }

    public ApiException(Type type, String message) {
        super(type.name() + " " + message);
        this.type = type;
    }

    public ApiException(Type type, String message, Throwable cause) {
        super(type.name() + " " + message, cause);
        this.type = type;
    }

    public ApiException(Type type, Throwable cause) {
        super(type.name(), cause);
        this.type = type;
    }

    public ApiException(Type type, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(type.name() + " " + message, cause, enableSuppression, writableStackTrace);
        this.type = type;
    }
}
