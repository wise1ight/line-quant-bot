package kr.kuvh.linebot.altcoin.bot.entity;

import kr.kuvh.linebot.altcoin.bot.common.TradingStatusEnum;
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
@Table(name = "cc_trading_log")
public class TradingLogEntity implements Serializable {
    private static final long serialVersionUID = -2290926165929851257L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "session_id")
    private long sessionId;

    @Column(name = "status")
    private TradingStatusEnum status;

    @Column(name = "timestamp")
    private long timestamp;
}
