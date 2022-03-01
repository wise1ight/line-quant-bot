package kr.kuvh.linebot.altcoin.bot.entity;

import kr.kuvh.linebot.altcoin.bot.common.TransferStatusEnum;
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
@Table(name = "cc_transfer_log")
public class TransferLogEntity implements Serializable {
    private static final long serialVersionUID = 2903744469181210569L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "currency_id")
    private long currencyId;

    //각 Currency의 수량임.. BTC가 될 수도 있고 LTC가 될 수도 있고 등등..
    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "dept_exchange_id")
    private long deptExchangeId;

    @Column(name = "dest_exchange_id")
    private long destExchangeId;

    @Column(name = "status")
    private TransferStatusEnum status;

    @Column(name = "timestamp")
    private long timestamp;

}
