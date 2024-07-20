package com.springboot.reply.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Getter
@Builder
public class ReplyPostDto {
    private long boardId;
    @NotNull
    private String comment;

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
}
