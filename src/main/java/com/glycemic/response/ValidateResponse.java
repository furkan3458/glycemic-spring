package com.glycemic.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateResponse {
	
	private boolean result;
	private Integer status;
	private String message;
	
}
