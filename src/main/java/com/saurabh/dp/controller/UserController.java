package com.saurabh.dp.controller;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.data.Tag;
import com.saurabh.dp.model.DataDtls;
import com.saurabh.dp.service.DataService;
import com.saurabh.dp.service.UserService;

// Handling the user-related actions for /user   url
@Controller
@RequestMapping("/user") // mapping url starting with /user to controller
public class UserController {
    
	@Autowired
	private UserService userService;
	
	@Autowired
	private DataService dataService;
	
 
	
	@GetMapping("/patient")
	public String patient() {
		
		return  "patient";
	}
	
	/* .... Patient Details Creation .... */
	@PostMapping("/createData")
	public String createdata(@ModelAttribute DataDtls data, HttpSession session) throws IOException{
		
		
		LocalDate currentDate = LocalDate.now();
		
		LocalDate dateOfBirth = LocalDate.parse(data.getDateOfBirth());
		
		if(dateOfBirth.isAfter(currentDate)) {
            session.setAttribute("msg", "Invalid Date of Birth. Please choose a valid date.");
            return "redirect:/user/patient";
		}
		
		boolean f = dataService.chkEmail(data.getEmail());		
		
		if(f) {
			session.setAttribute("msg", "Email id already exists");
		}
		else {
			DataDtls dataDtls = dataService.createData(data);
			
			if(dataDtls != null) {
				//session.setAttribute("msg", "Details Stored Successfully");
			}
			else {
				session.setAttribute("msg", "Something wrong on server");
			}
		}
		
		return "redirect:/user/";
	}

	
	/*  Deleting Data */
	@GetMapping("/delete/{id}")
	public String deleteData(@PathVariable("id") int id) {
		dataService.deleteData(id);
		
		return "redirect:/user/";
	}
	
	/* Updating Data */
	@GetMapping("/update/{id}")
	public String showUpdateForm(@PathVariable("id") int id, Model model) {
		 DataDtls data = dataService.getDataById(id);
		 model.addAttribute("data", data);
		 
		 return "update";
	}
	
	/*  .... Updation .....*/
	@PostMapping("/update/{id}")
	public String saveUpdatedData(@PathVariable int id, @ModelAttribute DataDtls data, @RequestParam(name = "action", required = false) String action) {
		 
		if("save".equals(action)) {
			dataService.updateData(id, data);
		}
		return "redirect:/user/";
	}
	
	@GetMapping("/")
	public String viewPatients(Model model) {
		return findPaginated(1, "patientName", "asc", model);
	}	
	
	/* .....  Pagination and Sorting .......*/
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable (value = "pageNo") int pageNo,
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 10;
		
		Page<DataDtls> page = dataService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<DataDtls> listData = page.getContent();
		
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");	
		
		model.addAttribute("listData", listData);
		
		return "view";
	}
	
	/* ... for uploading file ....... */
	@PostMapping("/uploadFile/{id}")
	public String uploadFile(@PathVariable int id, @RequestParam("file") MultipartFile file, @RequestParam("uploadDate") String uploadDate) {
		 
		dataService.uploadFile(id, file, uploadDate);
		
		return "redirect:/user/";
	}
	
	/* ... for downloading file....
	@GetMapping("/downloadFile/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable int id){
		return dataService.downloadFile(id);
	}
*/
	@GetMapping("/fupload/{id}")
	public String uploadDownloadFile(@PathVariable int id ,Model model) {
		DataDtls data = dataService.getDataById(id);
		
   	model.addAttribute("data", data);
		
		return "fupload";
	} 	  
	
	
	/* Re-routing to another app */
	
	@GetMapping("/react")
	public void handleGet(HttpServletResponse response) {
	    response.setHeader("Location", "http://localhost:3000/");
	    response.setStatus(302);
	}
	
	@GetMapping("/test/{id}")
	public ResponseEntity<byte[]> getView(@PathVariable int id){
		try {
		DataDtls data = dataService.getDataById(id);
		
		String inputDicomFilePath = data.getFilePath();
		
	    File file = new File(inputDicomFilePath);
	    DicomInputStream dis = new DicomInputStream(file);
	    Attributes attributes = dis.readDataset();
	      
    	ImageInputStream iis = new FileImageInputStream(new File(inputDicomFilePath));
        
        ImageReader reader = ImageIO.getImageReadersByFormatName("DICOM").next();

        reader.setInput(iis);

        BufferedImage image = reader.read(0, new ImageReadParam());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        // Return the image as part of the HTTP response
        
//        File outputImageFile = new File("C:\\Tejas\\Dicom data\\output.png");
//        ImageIO.write(image, "png", outputImageFile);
//        System.out.println("Image saved ");

        // Close the input stream
        iis.close();
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}
	catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		 return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
         }
		
}
	
	/*
	@GetMapping("/tst")
	public String test() {
		return "tmp";
	}
	  */
	/*  28 - 04 -2023 */
	
 /*   @GetMapping(value = "/viewDicomFile/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewDicomFile(@PathVariable int id) {
        DataDtls data = dataService.getDataById(id);

        if (data != null && data.isFileUploaded()) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", data.getId());
          //  response.put("dicomFileData", data.getFileData());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + data.getFileName())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }  */
    
    /*    */
}
