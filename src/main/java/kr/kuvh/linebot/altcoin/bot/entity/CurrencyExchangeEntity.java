package kr.kuvh.linebot.altcoin.bot.entity;

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
@Table(name = "cc_currency_exchange")
public class CurrencyExchangeEntity implements Serializable {
    private static final long serialVersionUID = -9216483962561497523L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "exchange_id")
    private long exchangeId;

    @Column(name = "exchange_currency")
    private String exchangeCurrency;

    @Column(name = "support_currency_id")
    private long supportCurrencyId;
}
