package kr.kuvh.linebot.altcoin.bot.repository;

import kr.kuvh.linebot.altcoin.bot.entity.BitcoinWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BitcoinWalletRepository extends JpaRepository<BitcoinWalletEntity, Long> {

    @Query("SELECT w FROM BitcoinWalletEntity w where w.userId = ?1")
    public List<BitcoinWalletEntity> findWalletByUserId(long userId);
}
