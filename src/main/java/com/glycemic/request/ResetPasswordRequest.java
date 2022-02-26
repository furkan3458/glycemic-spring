package com.glycemic.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordRequest implements Serializable{

	private static final long serialVersionUID = 1181918304641936306L;
	
	private String email, forgetKey;
	private String password, passwordConfirm;
}
