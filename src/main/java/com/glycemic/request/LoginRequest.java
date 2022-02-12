package com.glycemic.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest implements Serializable{
	
	private static final long serialVersionUID = 7256070083621818341L;

	private String email,password;
	private Boolean rememberMe;
}
