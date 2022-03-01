package kr.kuvh.linebot.altcoin.bot.repository;

import kr.kuvh.linebot.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m where m.lineUserId = ?1")
    public List<Member> findMemberByLineUserId(String lineUserId);
}
