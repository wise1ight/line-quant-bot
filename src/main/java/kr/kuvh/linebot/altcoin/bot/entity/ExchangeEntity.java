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
@Table(name = "cc_exchange")
public class ExchangeEntity implements Serializable {
    private static final long serialVersionUID = -1686894846369330370L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "exchange_name")
    private String exchangeName;
}
