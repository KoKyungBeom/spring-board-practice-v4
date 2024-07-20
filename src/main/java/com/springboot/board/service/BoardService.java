package com.springboot.board.service;

import com.springboot.auth.utils.Principal;
import com.springboot.board.entity.Board;
import com.springboot.board.entity.Like;
import com.springboot.board.entity.Read;
import com.springboot.board.repository.BoardRepository;
import com.springboot.board.repository.LikeRepository;
import com.springboot.board.repository.ReadRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final ReadRepository readRepository;
    public BoardService(BoardRepository boardRepository, LikeRepository likeRepository, MemberRepository memberRepository, ReadRepository readRepository) {
        this.boardRepository = boardRepository;
        this.likeRepository = likeRepository;
        this.memberRepository = memberRepository;
        this.readRepository = readRepository;
    }

    public Board createBoard(Board board,Authentication authentication) {
        Principal principal = (Principal) authentication.getPrincipal();

        String email = principal.getUsername();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        Member findMember = optionalMember.orElseThrow(()->new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        board.setMember(findMember);

        return boardRepository.save(board);
    }

    public Board updateBoard(Board board,Authentication authentication) {
        verifyAuthentication(board.getBoardId(), authentication);

        Board findBoard = findBoard(board.getBoardId());

            Optional.ofNullable(board.getTitle())
                    .ifPresent(title -> findBoard.setTitle(title));
            Optional.ofNullable(board.getContent())
                    .ifPresent(content -> findBoard.setContent(content));
            Optional.ofNullable(board.getPrivacyStatus())
                    .ifPresent(privacyStatus -> findBoard.setPrivacyStatus(privacyStatus));

            board.setModifiedAt(LocalDateTime.now());

        return boardRepository.save(findBoard);
    }
    public Board newFindBoard(long boardId, Authentication authentication) {
        Principal principal = (Principal) authentication.getPrincipal();

        long memberId = principal.getMemberId();

        List roles = (List) authentication.getAuthorities();

        System.out.printf("%s",roles.get(0));

        Board board = findVerifiedBoard(boardId);

        if (board.getQuestionStatus() == Board.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
        }

        if (board.getPrivacyStatus() != Board.PrivacyStatus.PRIVACY_PUBLIC){
            if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                createRead(boardId,authentication);
                return findVerifiedBoard(boardId);
            }
            if(board.getMember().getMemberId() != memberId) {
                throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
            }
        }
        createRead(boardId,authentication);

        return findVerifiedBoard(boardId);
    }

    public Board findBoard(long boardId) {
        Board board = findVerifiedBoard(boardId);

        if (board.getQuestionStatus() == Board.QuestionStatus.QUESTION_DELETED) {
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
        }
        return findVerifiedBoard(boardId);
    }
    public Page<Board> findBoards(int page, int size, String sort, String standard) {

        Pageable pageable = createPageable(page,size,sort,standard);

        return boardRepository.findByQuestionStatusNotAndQuestionStatusNot(pageable,Board.QuestionStatus.QUESTION_DELETED,Board.QuestionStatus.QUESTION_DEACTIVED);
    }

    public void deleteBoard(long boardId,Authentication authentication) {

        verifyAuthentication(boardId,authentication);

        Board findBoard = findBoard(boardId);

        if (findBoard.getQuestionStatus() == Board.QuestionStatus.QUESTION_DELETED) {
           throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_QUESTION_STATUS);
        }

        findBoard.setQuestionStatus(Board.QuestionStatus.QUESTION_DELETED);

        if (findBoard.getReply() != null) {
            findBoard.removeReply(findBoard.getReply());
        }

        boardRepository.save(findBoard);
    }
    public void toggleLike(long boardId, long memberId) {

        Optional<Board> board = boardRepository.findById(boardId);
        Optional<Member> member = memberRepository.findById(memberId);

        Board findBoard = board.orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        Member findMember = member.orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Optional<Like> optionalLike = likeRepository.findByMemberAndBoard(findMember, findBoard);

        if (optionalLike.isPresent()) {
            Like findLike = optionalLike.orElseThrow(() -> new BusinessLogicException(ExceptionCode.REPLY_EXISTS));
            findMember.removeLike(findLike);
            findBoard.removeLike(findLike);
            findBoard.decreasedLikeCount();
            likeRepository.delete(findLike);
        } else {
            Like addLike = new Like();
            addLike.setBoard(findBoard);
            addLike.setMember(findMember);
            findBoard.increasedLikeCount();
            likeRepository.save(addLike);
        }
    }

    public Board findVerifiedBoard(long boardId) {

        Optional<Board> Board = boardRepository.findById(boardId);

        return Board.orElseThrow(()-> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
    }
    public void verifyAuthentication(long boardId,Authentication authentication) {

        Principal principal = (Principal) authentication.getPrincipal();

        String email = principal.getUsername();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        Member findMember = optionalMember.orElseThrow(()->new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Board findBoard = findBoard(boardId);

        if(findBoard.getMember() != findMember){
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
    }
    public void createRead (long boardId, Authentication authentication){
        Board findBoard = findBoard(boardId);

        Principal principal = (Principal) authentication.getPrincipal();

        Optional<Member> optionalMember = memberRepository.findById(principal.getMemberId());

        Member findMember = optionalMember.orElseThrow( () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Optional<Read> optionalRead = readRepository.findByMemberAndBoard(findMember,findBoard);

        if (optionalRead.isPresent()) {
            Read findRead = optionalRead.orElseThrow(() -> new BusinessLogicException(ExceptionCode.REPLY_EXISTS));
        } else {
            Read addRead = new Read();
            addRead.setBoard(findBoard);
            addRead.setMember(findMember);
            findBoard.increasedReadCount();
            readRepository.save(addRead);
        }
    }
    public Pageable createPageable(int page, int size, String sort, String standard){
        if (sort.equals("DESC")){
            return PageRequest.of(page,size,Sort.Direction.DESC,standard);
        }
        return PageRequest.of(page,size,Sort.Direction.ASC,standard);
    }
}
