package com.FileQuash.Controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.FileQuash.Machines.Quasher;

import jakarta.servlet.http.HttpServletRequest;

@Controller

public class MainController {

	private byte[] processedFile;
	private Quasher quasher;
	private int flag=0;
	@Autowired
	public MainController(Quasher quasher) {
			this.quasher =  quasher;
	}
	
	@GetMapping("/")
	public String getDashboard() {
		return "index";
	}
	
	@PostMapping("/compress-file")
	public String compressFile(@RequestParam("file") MultipartFile file,HttpServletRequest request,  Model m1) {
		
		if(file.isEmpty()) {
			m1.addAttribute("message","File is empty");
			return  "error_page";
		}
		
		if(! file.getContentType().equals("text/plain")) {
			m1.addAttribute("message","File should be of type text");
			return  "error_page";
		}
		
		String action = request.getParameter("submit-click");
		
		if(action.equals("compress")) {
			processedFile =  quasher.fileCompress(file);
			flag=0;
		}
		else {
			System.out.println("dECOMPRESSION STRATR");
			processedFile =  quasher.fileDecompress(file);
			flag=1;
		}
		m1.addAttribute("flag",flag);
		
		double result = (double) file.getSize() / 1024;
		System.out.println((String.format("%.2f", result)+"KB"));
		m1.addAttribute("orignal_size" ,(String.format("%.2f", result)+"KB"));
		
		ByteArrayInputStream i = new ByteArrayInputStream(processedFile);
		result = (double) i.available() / 1024;
		System.out.println((String.format("%.2f", result)+"KB"));
		m1.addAttribute("new_size" ,(String.format("%.2f", result)+"KB"));
		
		
		System.out.println("Bro Compression is successfull");
		m1.addAttribute("isProcessed",true);
		
		return "index";
	}
	
	@GetMapping("/download-file")
	public ResponseEntity<InputStreamResource> downloadProcessedFile() throws IOException {
        if (processedFile == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Create InputStreamResource from the serialized content
        ByteArrayInputStream inputStream = new ByteArrayInputStream(processedFile);
        
        // Set headers to download the file as .txt
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + ((flag==0)? "compressed-file.txt": "decompressed-file.txt"));
        headers.setContentType(MediaType.TEXT_PLAIN);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(processedFile.length)
                .body(new InputStreamResource(inputStream));
    }
}
