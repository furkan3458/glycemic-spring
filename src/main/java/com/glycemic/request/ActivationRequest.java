package com.glycemic.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivationRequest implements Serializable{

	private static final long serialVersionUID = 1144332256337856437L;
	
	private String email,activateKey;

}
