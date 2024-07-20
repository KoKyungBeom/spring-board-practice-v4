package com.springboot.board.repository;

import com.springboot.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByQuestionStatusNotAndQuestionStatusNot (Pageable pageable, Board.QuestionStatus questionStatus1, Board.QuestionStatus questionStatus2);
}
