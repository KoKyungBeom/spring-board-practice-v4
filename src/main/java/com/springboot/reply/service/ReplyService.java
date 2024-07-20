package com.springboot.reply.service;

import com.springboot.auth.utils.Principal;
import com.springboot.board.entity.Board;
import com.springboot.board.repository.BoardRepository;
import com.springboot.board.service.BoardService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.service.MemberService;
import com.springboot.reply.entity.Reply;
import com.springboot.reply.repository.ReplyRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final MemberService memberService;
    private final BoardService boardService;
    public ReplyService(ReplyRepository replyRepository, MemberService memberService, BoardService boardService) {
        this.replyRepository = replyRepository;
        this.memberService = memberService;
        this.boardService = boardService;
    }
    public Reply createReply(Reply reply, Authentication authentication) {

        Principal principal = (Principal) authentication.getPrincipal();

        long memberId = principal.getMemberId();

        Board findBoard = boardService.findBoard(reply.getBoard().getBoardId());

        if (findBoard.getReply() != null){
            throw new BusinessLogicException(ExceptionCode.REPLY_EXISTS);
        }

        if (findBoard.getMember().getMemberId() == memberId) {

            findBoard.setQuestionStatus(Board.QuestionStatus.QUESTION_ANSWERED);

            reply.setMember(memberService.findMember(memberId));
        }

        return replyRepository.save(reply);
    }
    public Reply updateReply(Reply reply, Authentication authentication){
        Principal principal = (Principal) authentication.getPrincipal();

        long memberId = principal.getMemberId();

        Reply findReply = findVerifiedReply(reply.getReplyId());

        if(reply.getBoard().getMember().getMemberId() == memberId) {

            Optional.ofNullable(reply.getComment())
                    .ifPresent(comment -> findReply.setComment(comment));

            reply.setModifiedAt(LocalDateTime.now());

        }

        return replyRepository.save(findReply);
    }
    public Reply findReply(long replyId){
        return findVerifiedReply(replyId);
    }

    public void verifyExistReply(long replyId){
        Optional<Reply> Reply  = replyRepository.findById(replyId);
        if(Reply.isPresent()){
            throw new BusinessLogicException(ExceptionCode.REPLY_EXISTS);
        }
    }
    public Reply findVerifiedReply(long replyId){
        Optional<Reply> findReply = replyRepository.findById(replyId);
        return findReply.orElseThrow(()-> new BusinessLogicException(ExceptionCode.REPLY_NOT_FOUND));
    }

    public void deleteReply(long replyId, Authentication authentication){

        Principal principal = (Principal) authentication.getPrincipal();

        long memberId = principal.getMemberId();

        Reply findReply = findVerifiedReply(replyId);

        if (findReply.getMember().getMemberId() == memberId){
            replyRepository.delete(findReply);
        }
    }
}
