package kr.kuvh.linebot.altcoin.bot.entity;

import kr.kuvh.linebot.altcoin.bot.common.TradingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cc_trading_session")
public class TradingSessionEntity implements Serializable {
    private static final long serialVersionUID = 568494261912340192L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "exchange_id")
    private long exchangeId;

    @Column(name = "exchange_market_currency")
    private String exchangeMarketCurrency;

    @Column(name = "exchange_currency")
    private String exchangeCurrency;

    @Column(name = "trading_type")
    private TradingTypeEnum tradingType;
}
