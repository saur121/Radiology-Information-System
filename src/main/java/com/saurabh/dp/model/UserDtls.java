package com.saurabh.dp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.*;

// lombok internally getter setter call kr leta h

// Representing the schema of the table

@Data
@Entity
public class UserDtls {
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String fullName;
	
	private String email;
	
	//private String address;
	
	//private String qualification;
	
	private String password;
	
	private String role;
	
}
