package com.bots.lvivcroissantbot.entity.register;

import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CustomerOrdering;
import com.bots.lvivcroissantbot.constantenum.telegram.TelegramUserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TUser {
    @Id
    @GeneratedValue
    private Long id;
    private Integer chatId;
    private String name;
    private String lastName;
    private String locale;
    private String userName;
    private TelegramUserStatus status;
    private Integer countCompletingOrderingsForCourier;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "tUser",cascade = CascadeType.ALL)
    private List<CustomerOrdering>customerOrderings = new ArrayList<>();

    @OneToMany(mappedBy = "tUser",cascade = CascadeType.ALL)
    private List<CroissantEntity> ownCroissantEntities = new ArrayList<>();

    @OneToMany(mappedBy = "userCourier",cascade = CascadeType.ALL)
    private List<CustomerOrdering>courierCustomerOrderings = new ArrayList<>();

    public void addCroissant(CroissantEntity croissantEntity){
        ownCroissantEntities.add(croissantEntity);
        croissantEntity.setTUser(this);
    }
    public void addCustomerOrdering(CustomerOrdering customerOrdering){
        this.customerOrderings.add(customerOrdering);
        customerOrdering.setTUser(this);
    }

    public void addCourierOrdering(CustomerOrdering customerOrdering){
        courierCustomerOrderings.add(customerOrdering);
        customerOrdering.setTUser(this);
    }


}
