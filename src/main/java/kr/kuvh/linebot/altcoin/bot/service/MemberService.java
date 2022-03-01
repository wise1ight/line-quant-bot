package kr.kuvh.linebot.altcoin.bot.service;

import kr.kuvh.linebot.entity.Member;
import kr.kuvh.linebot.altcoin.bot.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class MemberService implements IMemberService {

    private MemberRepository memberRepository;

    @Autowired
    private void setMemberRepository(MemberRepository memberRepository) { this.memberRepository = memberRepository; }

    @Override
    public Member addMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public List<Member> findMemberByLineUserId(String lineUserId) {
        return memberRepository.findMemberByLineUserId(lineUserId);
    }

    @Override
    public boolean isUserExistByLineUserId(String lineUserId) {
        List<Member> members = findMemberByLineUserId(lineUserId);
        return members.size() != 0;
    }

}
