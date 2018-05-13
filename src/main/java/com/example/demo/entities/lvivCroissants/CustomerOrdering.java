package com.example.demo.entities.lvivCroissants;

import com.example.demo.entities.peopleRegister.CourierRegister;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.enums.messengerEnums.PaymentWay;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CustomerOrdering {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String phoneNumber;
    private String name;
    private String address;
    private String time;
    private Integer price;
    private PaymentWay paymentWay;
    private String completedTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    private List<String>croissants = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "customer_ordering_id")
    private CourierRegister courierRegister;

    @ManyToOne
    @JoinColumn(name = "tUser_id")
    private TUser tUser;
    @ManyToOne
    @JoinColumn(name = "courier_id")
    private TUser courier;
    @Override
    public String toString() {
        return "Замовлення № "+id+"\nзамовник: "+name+"\nНомер телефону: "+phoneNumber+"\nAddress: "+address+"\nTime: "+time+"\nprice: "+price+"\nOrder: "+croissants+"\n\n\n";
    }
}
