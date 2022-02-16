package com.glycemic.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;
import com.glycemic.validator.NutritionalIdValidator;
import com.glycemic.validator.NutritionalValidator;
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
@JsonView(NutritionalView.ExceptFood.class)
public class Nutritional extends BaseModel implements Serializable {

	private static final long serialVersionUID = 1859918222450635883L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Min(value = 1, message = "Id kısmı 1 den küçük olamaz.", groups=NutritionalIdValidator.class)
	private Long id;
	
	@NotNull(message="İsim kısmı boş bırakılamaz", groups=NutritionalValidator.class)
	private String name;
	
	@NotNull(message="Birim kısmı boş bırakılamaz", groups=NutritionalValidator.class)
	private String unit;

}
