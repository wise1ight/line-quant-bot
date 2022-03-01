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
@Table(name = "bittrex_sell_volume")
public class BittrexSellVolume implements Serializable {

    private static final long serialVersionUID = -1936778244178680319L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "market_name")
    private String marketName;

    @Column(name = "one_hour")
    private double oneHour;

    @Column(name = "three_hour")
    private double threeHour;

    @Column(name = "six_hour")
    private double sixHour;

    @Column(name = "half_day")
    private double halfDay;

    @Column(name = "one_day")
    private double oneDay;

    @Column(name = "three_day")
    private double threeDay;

    @Column(name = "one_week")
    private double oneWeek;
}
