package com.bots.lvivcroissantbot.entity.register;

import com.bots.lvivcroissantbot.constantenum.AccountStatus;
import com.bots.lvivcroissantbot.constantenum.messenger.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String phoneNumber;
    private AccountStatus status;
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TUser tUser;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MUser mUser;
}
