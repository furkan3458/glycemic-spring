package com.glycemic.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class JwtSession implements Serializable{
	
	private static final long serialVersionUID = 8937958151681015389L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(cascade = CascadeType.MERGE) 
	@NotNull
	private Users users;
	
	@NotNull
	private String jwttoken;
	
	private Long expiretime;
	
	public JwtSession(Users users, String jwttoken, Long expiretime) {
		this.users = users;
		this.jwttoken = jwttoken;
		this.expiretime = expiretime;
	}
	
	@Override
	public String toString(){
		return "";
	}
}
