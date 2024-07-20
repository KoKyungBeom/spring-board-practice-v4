package com.springboot.reply.dto;

import com.springboot.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReplyPatchDto {
    private long boardId;
    private long replyId;
    private String comment;
    private Board board;

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }
    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }
    public void setBoard(Board board) {
        this.board = board;
    }
}
