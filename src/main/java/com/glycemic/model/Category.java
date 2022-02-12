package com.glycemic.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.glycemic.validator.CategoryIdValidator;
import com.glycemic.validator.CategoryValidator;

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
public class Category extends BaseModel implements Serializable{

	private static final long serialVersionUID = 8290255678413836321L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Min(value = 1, message = "Id kısmı 1 den küçük olamaz.", groups=CategoryIdValidator.class)
	private Long id;
	
	@NotNull(message="İsim kısmı boş bırakılamaz.", groups=CategoryValidator.class)
	private String name;
	
	private String url;
	
	@Transient
	private List<Food> foods;
	
}
