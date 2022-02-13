package com.glycemic.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse implements Serializable{

	private static final long serialVersionUID = -4985856907026299901L;
	
	private Long id;
	private String token,email,fullname,name,surname,createdBy,modifiedBy;
	private Long createdDate, modifiedDate;
	private Boolean enable;
}
