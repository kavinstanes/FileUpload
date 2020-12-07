package com.assessment.spring.files.upload.service;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

	private final Path root;
	private final String csvFilePath;

	@Autowired
	public FilesStorageServiceImpl(@Value("${spring.location}") String location,
			@Value("${spring.filePath}") String filePath) {
		this.root = Paths.get(location);
		this.csvFilePath = filePath;
	}

	@Override
	public void save(MultipartFile file,Boolean isFileExist) {
		try {
			Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
			FileWriter pw = new FileWriter(this.csvFilePath,true);
			if(!isFileExist) {
				pw.append("FILE NAME");
				pw.append(",");
	            pw.append("FILE SIZE");
				pw.append(",");
	            pw.append("DATE UPLOADED");
				pw.append(",");

	            pw.append("TIME UPLOADED");
	            pw.append("\n");
			}
			
	        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
	        String formattedTime = timeStamp.toString();
	        String[] dateTime = formattedTime.split(" ");
			
			pw.append(file.getOriginalFilename());
			pw.append(",");
            pw.append(file.getSize()*0.000001+"MB");
            pw.append(",");
            pw.append(dateTime[0]);
            pw.append(",");
            pw.append(dateTime[1]);

            pw.append("\n");
            pw.flush();
            pw.close();
		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}

	@Override
	public Resource load(String filename) {
		try {
			Path file = root.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(root.toFile());
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
		} catch (IOException e) {
			throw new RuntimeException("Could not load the files!");
		}
	}

}
