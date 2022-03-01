package kr.kuvh.linebot.altcoin.bot.service;

import kr.kuvh.linebot.altcoin.bot.entity.BitcoinWalletEntity;

import java.util.List;

public interface IBitcoinWalletService {
    BitcoinWalletEntity addWallet(BitcoinWalletEntity wallet);
    List<BitcoinWalletEntity> findWalletByUserId(long userId);
    boolean isExistUserWallet(long userId);
}
