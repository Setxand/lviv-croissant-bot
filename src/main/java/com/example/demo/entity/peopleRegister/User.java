package com.example.demo.entity.peopleRegister;

import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.constantEnum.messengerEnums.Roles;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long recipientId;
    private String name;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Roles role;
    private String status;
    private String email;
    private Locale locale;
    private String picture;

    @ElementCollection
    private List<Long> ownCroissantsId = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CustomerOrdering> customerOrderings = new ArrayList<>();

    public User(Long recipientId, String name, String phoneNumber, String address) {
        this.recipientId = recipientId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public User() {
    }
    public void addCustomerOrdering(CustomerOrdering customerOrdering){
        customerOrderings.add(customerOrdering);
        customerOrdering.setUser(this);
    }

}
