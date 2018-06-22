package com.bots.lviv_croissant_bot.entity.register;

import com.bots.lviv_croissant_bot.entity.lvivCroissants.CustomerOrdering;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long recipientId;
    private String name;
    private String phoneNumber;

    @OneToMany(mappedBy = "userCourier",cascade = CascadeType.ALL)
    private List<CustomerOrdering>customerOrderings = new ArrayList<>();

    public Courier(Long recipientId, String name, String phoneNumber) {
        this.recipientId = recipientId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Courier() {
    }

    public List<CustomerOrdering> getCustomerOrderings() {
        return customerOrderings;
    }

    public void setCustomerOrderings(List<CustomerOrdering> customerOrderings) {
        for(CustomerOrdering customerOrdering: customerOrderings){
            customerOrdering.setUserCourier(this);
        }
        this.customerOrderings = customerOrderings;
    }

    public void addOne(CustomerOrdering customerOrdering){
        this.getCustomerOrderings().add(customerOrdering);
        customerOrdering.setUserCourier(this);
    }


}
