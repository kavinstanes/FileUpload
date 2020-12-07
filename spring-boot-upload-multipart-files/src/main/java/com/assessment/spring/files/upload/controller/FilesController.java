package com.assessment.spring.files.upload.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.assessment.spring.files.upload.message.ResponseMessage;
import com.assessment.spring.files.upload.model.FileInfo;
import com.assessment.spring.files.upload.service.FilesStorageService;

@Controller
@CrossOrigin("${service.url}")
public class FilesController {

	@Autowired
	FilesStorageService storageService;
	private static final Logger LOGGER = LogManager.getLogger(FilesController.class);

	// SERVICE TO UPLOAD FILES
	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
		LOGGER.info("Inside FilesController -> uploadFile :: START ");

		String message = "";
		boolean isAnyFileExist = false; 
		
		try {
			if (file!=null && file.getSize() * 0.00000095367432 <= 300) {
				boolean exist = storageService.loadAll()
						.anyMatch(t -> t.getFileName().toString().equals(file.getOriginalFilename()));
				if (exist) {
					message = "File with the name: " + file.getOriginalFilename() + " already exist!";
					LOGGER.error("Exception at FilesController -> uploadFile :: FILE PRE-EXISTS");

				} else {
					
					if( storageService.loadAll().count() > 0   ) {
						isAnyFileExist = true;
					}
					
					storageService.save(file,isAnyFileExist);
					message = "Uploaded the file successfully: " + file.getOriginalFilename();
				}
			} else {
				message = "File size exceeded the maximum allowed size of 300MB";
				LOGGER.error("Exception at FilesController -> uploadFile :: FILE SIZE ERROR ");

			}
			
			LOGGER.info("Inside FilesController -> uploadFile :: END ");

			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			e.printStackTrace();
			LOGGER.error("Exception at FilesController -> uploadFile ::" + e.getMessage());

			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
	}

	//SERVICE TO LIST ALL FILES UPLOADED
	@GetMapping("/files")
	public ResponseEntity<List<FileInfo>> getListFiles() {
		LOGGER.info("Inside FilesController -> getListFiles :: START ");

		List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
			String filename = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

			return new FileInfo(filename, url);
		}).collect(Collectors.toList());

		LOGGER.info("Inside FilesController -> getListFiles :: END ");

		return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	}

	//SERVICE TO DOWNLOAD A SPECIFIC FILE FROM THE LISTED FILES
	@GetMapping("/files/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		LOGGER.info("Inside FilesController -> getFile :: START ");

		Resource file = storageService.load(filename);
		LOGGER.info("Inside FilesController -> getFile :: END ");

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
}
