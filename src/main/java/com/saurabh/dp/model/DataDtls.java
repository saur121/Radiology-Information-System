package com.saurabh.dp.model;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int id;
    
	public String patientName;
	
	public String gender;
	
	public String dateOfBirth;
	
	public String contactNumber;
	
	public String email;
	
	public String symptoms;
	
	public double temperature;
	
	 private boolean fileUploaded;
	 
	 private String uploadDate;
	 
	 private String fileName;
	 
	 private String filePath;
	
 	@Transient
	public MultipartFile file;
 	
 	
 	/* for metadata*/
    
	private String mName;
	
	private String mid;
	
	private String mage;
	
	private String mgender;
	
	private String mstudyDate;

 	  
}
/*	
	@Lob
	public byte[] fileData;
	
	public void setFile(MultipartFile file) throws IOException{
        this.file = file;
        if (file != null && !file.isEmpty()) {
            this.fileData = file.getBytes();
        } else {
            this.fileData = null; // Set fileData to null when no file is uploaded
        }
	}    */

