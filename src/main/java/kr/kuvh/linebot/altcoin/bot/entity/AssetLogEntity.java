package kr.kuvh.linebot.altcoin.bot.entity;

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
@Table(name = "cc_asset_log")
public class AssetLogEntity implements Serializable {
    private static final long serialVersionUID = -6558900853669342643L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "amount_btc")
    private BigDecimal amountBTC;

    @Column(name = "amount_krw")
    private long amountKRW;

    @Column(name = "amount_usd")
    private double amountUSD;

    @Column(name = "timestamp")
    private long timestamp;
}
