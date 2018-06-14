package com.example.demo.entity.peopleRegister;

import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CourierRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long recipientId;
    private String name;
    private String phoneNumber;

    @OneToMany(mappedBy = "courierRegister",cascade = CascadeType.ALL)
    private List<CustomerOrdering>customerOrderings = new ArrayList<>();

    public CourierRegister(Long recipientId, String name, String phoneNumber) {
        this.recipientId = recipientId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public CourierRegister() {
    }

    public List<CustomerOrdering> getCustomerOrderings() {
        return customerOrderings;
    }

    public void setCustomerOrderings(List<CustomerOrdering> customerOrderings) {
        for(CustomerOrdering customerOrdering: customerOrderings){
            customerOrdering.setCourierRegister(this);
        }
        this.customerOrderings = customerOrderings;
    }

    public void addOne(CustomerOrdering customerOrdering){
        this.getCustomerOrderings().add(customerOrdering);
        customerOrdering.setCourierRegister(this);
    }


}
