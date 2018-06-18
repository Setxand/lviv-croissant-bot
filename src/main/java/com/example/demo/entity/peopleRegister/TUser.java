package com.example.demo.entity.peopleRegister;

import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.example.demo.entity.lvivCroissants.CustomerOrdering;
import com.example.demo.constantEnum.messengerEnums.Role;
import com.example.demo.constantEnum.telegramEnums.TelegramUserStatus;
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

    @OneToMany(mappedBy = "courier",cascade = CascadeType.ALL)
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
        customerOrdering.setCourier(this);
    }


}
