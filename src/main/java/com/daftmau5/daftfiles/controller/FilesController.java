package com.daftmau5.daftfiles.controller;

import java.io.File;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.daftmau5.daftfiles.dto.DaftFile;
import com.daftmau5.daftfiles.dto.Directory;

@Controller
@RequestMapping("/")
public class FilesController {

	@Autowired
	public FilesController() {
	}

	@RequestMapping(value = "/{dir}", method = RequestMethod.GET)
	public String listFiles(@PathVariable("dir") String dir, Model model) {
		System.out.println("New req: "+dir);
		dir.replace("?&", "\\");
		File folder = new File("C:\\"+dir);
		System.out.println("Accessing "+folder.getPath());
		File[] listOfFiles = folder.listFiles();
		ArrayList<DaftFile> files = new ArrayList<>();
		ArrayList<Directory> directories = new ArrayList<>();

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

		model.addAttribute("dirs", directories);
		model.addAttribute("files", files);
		return "listFiles";
	}
}
