package com.daftmau5.daftfiles.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.daftmau5.daftfiles.dto.DaftFile;
import com.daftmau5.daftfiles.dto.Directory;
import com.daftmau5.daftfiles.service.FileStorageService;

@Controller
@RequestMapping("/")
public class FilesController {

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	public FilesController() {
	}

	@RequestMapping(value = "/{dir}", method = RequestMethod.GET)
	public String listFiles(@PathVariable("dir") String dir, Model model) {
		try {
			System.out.println("New req: " + dir);
			dir = dir.replace("+", "/");
			File folder = new File("C:\\" + dir);
			System.out.println("Accessing " + folder.getPath());
			File[] listOfFiles = folder.listFiles();
			ArrayList<DaftFile> files = new ArrayList<>();
			ArrayList<Directory> directories = new ArrayList<>();

			if (listOfFiles == null) {
				return "getOut";
			} else {
				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()) {
						DaftFile daftFile = new DaftFile();
						daftFile.setNome(listOfFiles[i].getName());
						files.add(daftFile);
					} else if (listOfFiles[i].isDirectory()) {
						Directory directory = new Directory();
						directory.setNome(listOfFiles[i].getName());
						directories.add(directory);
					}
				}

				model.addAttribute("actualDir", dir);
				model.addAttribute("dirs", directories);
				model.addAttribute("files", files);
				return "listFiles";
			}
		} catch (Exception e) {
			return "shitGetOut";
		}
	}

	@RequestMapping("/downloadFile/{file}")
	public ResponseEntity<Resource> downloadFile(@PathVariable("file") String file, HttpServletRequest request) {
		file = file.replace("+", "/");
		// Try to determine file's content type
		String contentType = null;
		try {
			// Load file as Resource
			Resource resource = fileStorageService.loadFileAsResource(file);
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			// Fallback to the default content type if type could not be determined
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (IOException ex) {
			System.out.println("Could not determine file type.");
			return (ResponseEntity<Resource>) ResponseEntity.badRequest();
		}
	}
}
