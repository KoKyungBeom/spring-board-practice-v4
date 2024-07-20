package com.springboot.reply.controller;

import com.springboot.reply.dto.ReplyPatchDto;
import com.springboot.reply.dto.ReplyPostDto;
import com.springboot.reply.dto.ReplyResponseDto;
import com.springboot.reply.entity.Reply;
import com.springboot.reply.mapper.ReplyMapper;
import com.springboot.reply.service.ReplyService;
import com.springboot.response.SingleResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/v1/boards/{board-id}/replies")
@Validated
public class ReplyController {
    private final ReplyService replyService;
    private final ReplyMapper replyMapper;

    public ReplyController(ReplyService replyService, ReplyMapper replyMapper) {
        this.replyService = replyService;
        this.replyMapper = replyMapper;
    }
    @PostMapping
    public ResponseEntity postReply(@PathVariable("board-id") @Positive long boardId,
                                    @Valid @RequestBody ReplyPostDto replyPostDto,
                                    Authentication authentication){
        replyPostDto.setBoardId(boardId);

        Reply reply = replyService.createReply(replyMapper.replyPostDtoToReply(replyPostDto),authentication);

        return new ResponseEntity(HttpStatus.OK);
    }
    @PatchMapping("/{reply-id}")
    public ResponseEntity patchReply(@PathVariable("board-id") @Positive long boardId,
                                     @PathVariable("reply-id") @Positive long replyId,
                                     @Valid @RequestBody ReplyPatchDto replyPatchDto,
                                     Authentication authentication) {
        replyPatchDto.setBoardId(boardId);
        Reply findReply = replyService.findVerifiedReply(replyId);
        replyPatchDto.setReplyId(replyId);
        replyPatchDto.setBoard(findReply.getBoard());
        Reply reply = replyService.updateReply(replyMapper.replyPatchDtoToReply(replyPatchDto), authentication);

        return new ResponseEntity(new SingleResponseDto<>(replyMapper.replyToReplyResponseDto(reply)),HttpStatus.OK);
    }

    @GetMapping("/{reply-id}")
    public ResponseEntity getReply(@PathVariable("reply-id") @Positive long replyId) {
        Reply findReply = replyService.findReply(replyId);

        ReplyResponseDto response = replyMapper.replyToReplyResponseDto(findReply);

        return new ResponseEntity(new SingleResponseDto<>(response),HttpStatus.OK);
    }

    @DeleteMapping("/{reply-id}")
    public ResponseEntity deleteReply(@PathVariable("reply-id") @Positive long replyId,
                                      Authentication authentication) {
        replyService.deleteReply(replyId, authentication);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
