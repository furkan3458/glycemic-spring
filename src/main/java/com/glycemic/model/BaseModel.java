package com.glycemic.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseModel implements Serializable {

	private static final long serialVersionUID = -7746273642912775124L;
	@CreatedDate
    private long createdDate;
    @LastModifiedDate
    private long modifiedDate;

    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String modifiedBy;

}
