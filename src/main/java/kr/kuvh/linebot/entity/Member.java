package kr.kuvh.linebot.entity;

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
@Table(name = "linebot_member")
public class Member implements Serializable {
    private static final long serialVersionUID = -5584157465359246341L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "line_user_id")
    private String lineUserId;

    @Column(name = "email")
    private String email;

    @Column(name = "email_confirm")
    private boolean emailConfirm;

    @Column(name = "pin_hash")
    private String pinHash;
}
