package com.glycemic.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.glycemic.serializer.CategorySerializer;
import com.glycemic.validator.CategoryIdValidator;
import com.glycemic.validator.CategoryValidator;
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
public class Category extends BaseModel implements Serializable, Cloneable{

	private static final long serialVersionUID = 8290255678413836321L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Min(value = 1, message = "Id kısmı 1 den küçük olamaz.", groups= {CategoryIdValidator.class})
	private Long id;
	
	@NotNull(message="İsim kısmı boş bırakılamaz.", groups=CategoryValidator.class)
	@JoinColumn(unique = true)
	private String name;
	
	private String url;
	
	@JsonSerialize(using = CategorySerializer.class)
	@Transient
	private List<Food> foods;
	
	public Category(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return "Category [id=" + id + ", name=" + name + ", url="+url+"]";
	}
	
	public Category copy() {
		try {
			return (Category) this.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
}
