package com.bots.lviv_croissant_bot.entity.register;

import com.bots.lviv_croissant_bot.constantEnum.AccountStatus;
import com.bots.lviv_croissant_bot.constantEnum.messengerEnum.Role;
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
