package kr.kuvh.linebot.altcoin.bot.service;

import kr.kuvh.linebot.entity.Member;

import java.util.List;

public interface IMemberService {
    Member addMember(Member member);
    List<Member> findMemberByLineUserId(String lineUserId);
    boolean isUserExistByLineUserId(String lineUserId);
}
