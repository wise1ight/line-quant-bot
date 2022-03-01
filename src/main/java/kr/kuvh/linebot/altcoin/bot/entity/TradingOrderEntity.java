package kr.kuvh.linebot.altcoin.bot.entity;

import kr.kuvh.linebot.altcoin.bot.common.OrderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cc_trading_order")
public class TradingOrderEntity implements Serializable {
    private static final long serialVersionUID = -50449026941155844L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "session_id")
    private long sessionId;

    @Column(name = "order_type")
    private OrderTypeEnum orderType;

    @Column(name = "price_btc")
    private BigDecimal priceBTC;

    @Column(name = "price_krw")
    private long priceKRW;

    @Column(name = "price_usd")
    private double priceUSD;

    @Column(name = "amount")
    private BigDecimal amount;
}
