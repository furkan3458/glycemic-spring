package com.glycemic.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;
import com.glycemic.validator.FoodNutritionalIdValidator;
import com.glycemic.validator.FoodNutritionalValidator;
import com.glycemic.view.NutritionalView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FoodNutritional extends BaseModel implements Serializable{

	private static final long serialVersionUID = 7939291708698036552L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Min(value = 1, message = "Id kısmı 1 den küçük olamaz.", groups=FoodNutritionalIdValidator.class)
	@JsonView(NutritionalView.ExceptFood.class)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.DETACH)
	@NotNull(message="Yiyecek alanı boş bırakılmaz.", groups=FoodNutritionalValidator.class)
	private Food food;
	
	@ManyToOne(cascade = CascadeType.DETACH)
	@NotNull(message="Besin değeri türü boş bırakılmaz.", groups=FoodNutritionalValidator.class)
	@JsonView(NutritionalView.ExceptFood.class)
	private Nutritional nutritional;
	
	@NotNull(message="Besin değerleri alanı boş bırakılmaz.", groups=FoodNutritionalValidator.class)
	@JsonView(NutritionalView.ExceptFood.class)
	private Integer rate;
}
