package com.springboot.board.repository;

import com.springboot.board.entity.Board;
import com.springboot.board.entity.Like;
import com.springboot.board.entity.Read;
import com.springboot.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReadRepository extends JpaRepository<Read, Long> {
    Optional<Read> findByMemberAndBoard (Member member, Board board);
}
