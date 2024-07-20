package com.springboot.board.mapper;

import com.springboot.board.dto.BoardPatchDto;
import com.springboot.board.dto.BoardPostDto;
import com.springboot.board.dto.BoardResponseDto;
import com.springboot.board.entity.Board;
import com.springboot.member.entity.Member;
import com.springboot.reply.entity.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    Board boardPostDtoToBoard(BoardPostDto boardPostDto);
//    @Mapping(source = "reply.comment", target = "comment")
//    @Mapping(source = "member.name", target = "username")
//    BoardResponseDto boardToBoardResponseDto (Board board);
    default BoardResponseDto boardToBoardResponseDto(Board board){

        Duration diff = Duration.between(board.getCreatedAt(), LocalDateTime.now());
        if( diff.toMinutes() > 2) {
            board.setIsNew(false);
        }
        BoardResponseDto.BoardResponseDtoBuilder boardResponseDto = BoardResponseDto.builder();
            boardResponseDto.comment(this.boardReplyComment(board));
            boardResponseDto.username(this.boardMemberName(board));
            boardResponseDto.title(board.getTitle());
            boardResponseDto.content(board.getContent());
            boardResponseDto.createdAt(board.getCreatedAt());
            boardResponseDto.modifiedAt(board.getModifiedAt());
            boardResponseDto.likeCount(board.getLikeCount());
            boardResponseDto.readCount(board.getReadCount());
            boardResponseDto.isNew(board.getIsNew());
            return boardResponseDto.build();
    }
    Board boardPatchDtoToBoard(BoardPatchDto boardPatchDto);
    List<BoardResponseDto> boardsToBoardResponseDtos(List<Board> boards);

    private String boardReplyComment(Board board) {
        if (board == null) {
            return null;
        } else {
            Reply reply = board.getReply();
            if (reply == null) {
                return null;
            } else {
                String comment = reply.getComment();
                return comment == null ? null : comment;
            }
        }
    }
    private String boardMemberName(Board board) {
        if (board == null) {
            return null;
        } else {
            Member member = board.getMember();
            if (member == null) {
                return null;
            } else {
                String name = member.getName();
                return name == null ? null : name;
            }
        }
    }
}
