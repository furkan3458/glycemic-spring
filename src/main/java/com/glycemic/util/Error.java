package com.glycemic.util;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Error implements Serializable{

	private static final long serialVersionUID = -7256538724438256922L;

	private HttpStatus httpStatus;
	
	private ErrorHandleType error;

	private String message;
	
	private String details;
	
    private String date;
    
    private String time;
    
    @Override
    public String toString() {
    	return "{\n\"httpStatus\":\""+httpStatus.value()+"\",\n\"error\":\""+error.value()+"\",\n\"message\":\""+message+"\",\n\"details\":\""+details+"\",\n\"date\":\""+date+"\",\n\"time\":\""+time+"\"\n}";
    }
}
