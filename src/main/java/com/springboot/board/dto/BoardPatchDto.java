package com.springboot.board.dto;

import com.springboot.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BoardPatchDto {
    private long boardId;
    private String title;
    private String content;
    private Board.PrivacyStatus privacyStatus;

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
}
