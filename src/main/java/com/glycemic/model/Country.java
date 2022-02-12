package com.glycemic.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.glycemic.validator.CountryIdValidator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table 
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Country extends BaseModel implements Serializable{

	private static final long serialVersionUID = 7727106370962024058L;
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Min(value = 1, message = "Id kısmı 1 den küçük olamaz.", groups=CountryIdValidator.class)
	private Long id;
	
	@NotNull(message="İsim kısmı boş bırakılamaz.")
	private String name;
}
