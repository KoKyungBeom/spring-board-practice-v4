package com.springboot.board.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class BoardPostDto {
    @NotNull
    private String title;
    @NotNull
    private String content;
}
