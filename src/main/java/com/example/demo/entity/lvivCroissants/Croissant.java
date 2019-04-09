package com.example.demo.entity.lvivCroissants;

import com.example.demo.entity.peopleRegister.TUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Croissant {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String type;
	private int price;
	private String imageUrl;
	private Long creatorId;
	@OneToMany(mappedBy = "croissant", cascade = CascadeType.ALL)
	private List<CroissantsFilling> croissantsFillings = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "tUser_id")
	private TUser tUser;


	public Croissant() {
	}

	public Croissant(String name, String type, List<CroissantsFilling> croissantsFillings) {
		this.name = name;
		this.type = type;
		for (CroissantsFilling croissantsFilling : croissantsFillings) {
			croissantsFilling.setCroissant(this);
		}
		this.croissantsFillings = croissantsFillings;

	}


	public Croissant(String name, String type) {
		this.name = name;
		this.type = type;

	}


	public void setCroissantsFillings(List<CroissantsFilling> croissantsFillings) {
		for (CroissantsFilling croissantsFilling : croissantsFillings) {
			croissantsFilling.setCroissant(this);
		}
		this.croissantsFillings = croissantsFillings;
	}

	public void addSingleFilling(CroissantsFilling croissantsFilling) {
		this.getCroissantsFillings().add(croissantsFilling);
		croissantsFilling.setCroissant(this);
	}

	@Override
	public String toString() {
		return "\n\n" + name + ".\n" + "" +
				"Вміст: \n" +
				croissantsFillings;
	}
}
