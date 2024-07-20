package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "Member not found"),
    MEMBER_EXISTS(409, "Member exists"),
    REPLY_EXISTS(409, "Reply exists"),
    BOARD_NOT_FOUND(404, "Board not found"),
    REPLY_NOT_FOUND(404, "Reply not found"),
    ACCESS_DENIED(403,"Only the author can delete this."),
    NOT_ADMIN(403,"This action requires administrator privileges."),
    CANNOT_CHANGE_QUESTION_STATUS(403, "QUESTION can not change"),
    NOT_IMPLEMENTATION(501, "Not Implementation");
    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
