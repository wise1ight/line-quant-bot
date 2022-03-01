package kr.kuvh.linebot.altcoin.bot.service;

import kr.kuvh.linebot.altcoin.bot.entity.BitcoinWalletEntity;
import kr.kuvh.linebot.altcoin.bot.repository.BitcoinWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BitcoinWalletService implements IBitcoinWalletService {

    private BitcoinWalletRepository bitcoinWalletRepository;

    @Autowired
    public void setBitcoinWalletRepository(BitcoinWalletRepository bitcoinWalletRepository) {
        this.bitcoinWalletRepository = bitcoinWalletRepository;
    }

    @Override
    public BitcoinWalletEntity addWallet(BitcoinWalletEntity wallet) {
        return bitcoinWalletRepository.save(wallet);
    }

    @Override
    public List<BitcoinWalletEntity> findWalletByUserId(long userId) {
        return bitcoinWalletRepository.findWalletByUserId(userId);
    }

    @Override
    public boolean isExistUserWallet(long userId) {
        List<BitcoinWalletEntity> members = findWalletByUserId(userId);
        return members.size() != 0;
    }
}
