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
	
	private String remoteAddr;
	
	private String userAgent;
	
	private String fingerPrint;
	
	public JwtSession(Users users, String jwttoken, Long expiretime, String remoteAddr, String userAgent, String fingerPrint) {
		this.users = users;
		this.jwttoken = jwttoken;
		this.expiretime = expiretime;
		this.remoteAddr = remoteAddr;
		this.userAgent = userAgent;
		this.fingerPrint = fingerPrint;
	}
	
	@Override
	public String toString(){
		return "";
	}
}
