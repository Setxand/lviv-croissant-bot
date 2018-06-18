package com.example.demo.entity.peopleRegister;

import com.example.demo.constantEnum.AccountStatus;
import com.example.demo.constantEnum.messengerEnums.Role;
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
