package com.bots.lviv_croissant_bot.entity.register;

import com.bots.lviv_croissant_bot.entity.lvivCroissants.CustomerOrdering;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Setter
public class MUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long recipientId;
    private String name;
    private String lastName;
    private String address;
    private String status;
    private String email;
    private Locale locale;
    private String picture;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ElementCollection
    private List<Long> ownCroissantsId = new ArrayList<>();

    @OneToMany(mappedBy = "mUser", cascade = CascadeType.ALL)
    private List<CustomerOrdering> customerOrderings = new ArrayList<>();

    public MUser(Long recipientId, String name, String address) {
        this.recipientId = recipientId;
        this.name = name;
        this.address = address;
    }

    public MUser() {
    }
    public void addCustomerOrdering(CustomerOrdering customerOrdering){
        customerOrderings.add(customerOrdering);
        customerOrdering.setMUser(this);
    }

}
