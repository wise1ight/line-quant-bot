package kr.kuvh.linebot.altcoin.bot.controller;

import com.linecorp.bot.model.message.TextMessage;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import kr.kuvh.linebot.altcoin.bot.entity.BitcoinWalletEntity;
import kr.kuvh.linebot.altcoin.bot.service.BitcoinRPCService;
import kr.kuvh.linebot.altcoin.bot.service.IBitcoinWalletService;
import kr.kuvh.linebot.altcoin.bot.service.IMemberService;
import kr.kuvh.linebot.annotation.ChatCommand;
import kr.kuvh.linebot.annotation.LineUserId;
import kr.kuvh.linebot.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WalletController
{
    @Autowired
    private IBitcoinWalletService bitcoinWalletService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private BitcoinRPCService bitcoinRPCService;

    @ChatCommand(command = "지갑개설", help = "지갑개설")
    public TextMessage createWallet(@LineUserId String lineUserId) {
        if(!memberService.isUserExistByLineUserId(lineUserId)) {
            return new TextMessage("먼저 회원가입을 해 주세요.");
        }

        List<Member> members = memberService.findMemberByLineUserId(lineUserId);
        Member member = members.get(0);

        if(bitcoinWalletService.isExistUserWallet(member.getId())) {
            return new TextMessage("이미 지갑이 개설되었습니다.");
        }

        String address = null;
        try {
            address = bitcoinRPCService.getClient().getNewAddress();
        } catch (BitcoindException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        }

        if(address == null) {
            return new TextMessage("월렛 서버 문제로 인하여, 지갑 개설에 문제가 발생하였습니다. 잠시 후 다시 시도해주세요.");
        }

        BitcoinWalletEntity entity = new BitcoinWalletEntity();
        entity.setUserId(member.getId());
        entity.setAddress(address);
        bitcoinWalletService.addWallet(entity);
        if(entity.getId() == 0) {
            return new TextMessage("일시적인 장애로 인하여 지갑 개설에 문제가 발생하였습니다. 잠시 후 다시 시도해주세요.");
        } else {
            return new TextMessage("지갑 개설이 완료되었습니다. 비트코인 지갑 주소는 " + address + " 입니다.");
        }
    }

    @ChatCommand(command = "지갑주소", help = "지갑주소")
    public TextMessage getWalletAddress(@LineUserId String lineUserId) {
        if(!memberService.isUserExistByLineUserId(lineUserId)) {
            return new TextMessage("먼저 회원가입을 해 주세요.");
        }

        List<Member> members = memberService.findMemberByLineUserId(lineUserId);
        Member member = members.get(0);

        if(bitcoinWalletService.isExistUserWallet(member.getId())) {
            List<BitcoinWalletEntity> wallets = bitcoinWalletService.findWalletByUserId(member.getId());
            BitcoinWalletEntity wallet = wallets.get(0);
            return new TextMessage("지갑 주소는 " + wallet.getAddress() + " 입니다.");
        } else {
            return new TextMessage("먼저 지갑 개설을 해 주세요.");
        }
    }
}
