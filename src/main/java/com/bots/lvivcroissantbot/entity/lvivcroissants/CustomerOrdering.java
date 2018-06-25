package com.bots.lvivcroissantbot.entity.lvivcroissants;

import com.bots.lvivcroissantbot.entity.register.Courier;
import com.bots.lvivcroissantbot.entity.register.TUser;
import com.bots.lvivcroissantbot.entity.register.MUser;
import com.bots.lvivcroissantbot.constantenum.messenger.PaymentWay;
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
    @JoinColumn(name = "mUser_id")
    private MUser mUser;

    @ElementCollection
    private List<String>croissants = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "customer_ordering_id")
    private Courier userCourier;

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
