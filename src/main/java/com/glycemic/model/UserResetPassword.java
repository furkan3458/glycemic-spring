package com.glycemic.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserResetPassword extends BaseModel implements Serializable{

	private static final long serialVersionUID = 4545742737036487649L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String uuid;
	
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(nullable = false)
	private Users user;
	
	@JoinColumn(columnDefinition = "boolean default false", nullable = false)
	private Boolean used;
}
