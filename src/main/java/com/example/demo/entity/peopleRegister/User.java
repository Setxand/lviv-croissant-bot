package com.example.demo.entity.peopleRegister;

import com.example.demo.constantEnum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.management.relation.Role;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String phoneNumber;
    private Status status;
    private Role role;

    @OneToOne(mappedBy = "user")
    private TUser tUser;

    @OneToOne(mappedBy = "user")
    private MUser mUser;
}
