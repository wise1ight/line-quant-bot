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
@Table(name = "bittrex_market_names")
public class BittrexMarketName implements Serializable{

    private static final long serialVersionUID = -8417326567534394092L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "market_name")
    private String marketName;

}
