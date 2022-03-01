package kr.kuvh.linebot.controller;

import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import kr.kuvh.linebot.annotation.ChatCommand;
import kr.kuvh.linebot.annotation.ChatParam;
import kr.kuvh.linebot.annotation.LineUserId;
import kr.kuvh.linebot.entity.Member;
import kr.kuvh.linebot.altcoin.bot.service.IMemberService;
import kr.kuvh.linebot.util.PinCodeUtil;
import kr.kuvh.linebot.util.ValidCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MemberController {

    private IMemberService memberService;
    private PinCodeUtil pinCodeUtil;

    @Autowired
    public MemberController(IMemberService memberService, PinCodeUtil pinCodeUtil) {
        this.memberService = memberService;
        this.pinCodeUtil = pinCodeUtil;
    }

    @ChatCommand(command = "회원가입 email pinCode", help = "회원가입 <이메일> <핀 번호 6자리>")
    public Message signUpMember(@LineUserId String lineUserId, @ChatParam("email") String email, @ChatParam("pinCode") String pinCode) {
        if(memberService.isUserExistByLineUserId(lineUserId)) {
            return new TextMessage("이미 회원가입된 사용자 입니다.");
        }

        //email 체크
        if(!ValidCheckUtil.isValidEmail(email)) {
            return new TextMessage("잘못된 이메일 주소 입니다.");
        }

        //pin 체크
        if(!ValidCheckUtil.isValidPinCode(pinCode)) {
            return new TextMessage("잘못된 PIN 코드 입니다.");
        }

        Member newMember = new Member();
        newMember.setLineUserId(lineUserId);
        newMember.setEmail(email);
        String pinHash = pinCodeUtil.encode(pinCode, email);
        newMember.setPinHash(pinHash);

        if(memberService.addMember(newMember).getId() != 0) {
            //return "이메일 " + email + "로 발송된 이메일을 확인 해 주시기 바랍니다.";
            return new TextMessage("정상적으로 등록되었습니다.");
        } else {
            return new TextMessage("일시적인 장애가 발생하였습니다. 다시 시도해 주시기 바랍니다.");
        }
    }
}
