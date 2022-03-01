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
@Table(name = "cc_currency_support")
public class CurrencySupportEntity implements Serializable {
    private static final long serialVersionUID = -2875585564404039049L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "currency_name")
    private String currencyName;
}
