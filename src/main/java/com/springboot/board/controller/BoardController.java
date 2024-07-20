package com.springboot.board.controller;

import com.springboot.auth.utils.Principal;
import com.springboot.board.dto.BoardPatchDto;
import com.springboot.board.dto.BoardPostDto;
import com.springboot.board.dto.BoardResponseDto;
import com.springboot.board.entity.Board;
import com.springboot.board.mapper.BoardMapper;
import com.springboot.board.service.BoardService;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/boards")
@Validated
public class BoardController {
    private final BoardService boardService;
    private final BoardMapper boardMapper;

    public BoardController(BoardService boardService, BoardMapper boardMapper) {
        this.boardService = boardService;
        this.boardMapper = boardMapper;
    }

    @PostMapping
    public ResponseEntity postBoard(@Valid @RequestBody BoardPostDto boardPostDto, Authentication authentication) {

        Board board = boardService.createBoard(boardMapper.boardPostDtoToBoard(boardPostDto), authentication);

        return new ResponseEntity(HttpStatus.CREATED);
    }
    @PostMapping("/{board-id}/like")
    public ResponseEntity postLike(@PathVariable("board-id") @Positive long boardId,
                                   Authentication authentication) {

        Principal principal = (Principal) authentication.getPrincipal();

        boardService.toggleLike(boardId, principal.getMemberId());

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/{board-id}")
    public ResponseEntity patchBoard(@PathVariable("board-id") @Positive long boardId,
                                     @Valid @RequestBody BoardPatchDto boardPatchDto,
                                     Authentication authentication) {
        boardPatchDto.setBoardId(boardId);

        Board board = boardService.updateBoard(boardMapper.boardPatchDtoToBoard(boardPatchDto), authentication);

        return new ResponseEntity(new SingleResponseDto<>(boardMapper.boardToBoardResponseDto(board)), HttpStatus.OK);
    }

    @GetMapping("/{board-id}")
    public ResponseEntity getBoard(@PathVariable("board-id") @Positive long boardId,
                                   Authentication authentication) {

        Board board = boardService.newFindBoard(boardId, authentication);

        BoardResponseDto response = boardMapper.boardToBoardResponseDto(board);

        return new ResponseEntity(new SingleResponseDto<>(response), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity getBoards(@Positive @RequestParam int page,
                                    @Positive @RequestParam int size,
                                    @RequestParam String sort,
                                    @RequestParam String standard) {
        Page<Board> pageBoards = boardService.findBoards(page - 1, size, sort, standard);

        List<Board> boards = pageBoards.getContent();

        return new ResponseEntity(new MultiResponseDto<>(boardMapper.boardsToBoardResponseDtos(boards), pageBoards), HttpStatus.OK);
    }
    @DeleteMapping("/{board-id}")
    public ResponseEntity deleteBoard(@PathVariable("board-id") @Positive long boardId,
                                      Authentication authentication) {
        boardService.deleteBoard(boardId, authentication);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
