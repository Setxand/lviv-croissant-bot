package com.bots.lvivCroissantBot.entity.lvivCroissants;

import com.bots.lvivCroissantBot.entity.register.Courier;
import com.bots.lvivCroissantBot.entity.register.TUser;
import com.bots.lvivCroissantBot.entity.register.MUser;
import com.bots.lvivCroissantBot.constantEnum.messengerEnum.PaymentWay;
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
    private Courier courier;

    @ManyToOne
    @JoinColumn(name = "tUser_id")
    private TUser tUser;
    @ManyToOne
    @JoinColumn(name = "courier_id")
    private TUser userCourier;
    @Override
    public String toString() {
        return "Замовлення № "+id+"\nзамовник: "+name+"\nНомер телефону: "+phoneNumber+"\nAddress: "+address+"\nTime: "+time+"\nprice: "+price+"\nOrder: "+croissants+"\n\n\n";
    }
}
