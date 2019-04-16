package com.theoaked.daftfiles.controller;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.theoaked.daftfiles.dto.DaftFile;
import com.theoaked.daftfiles.dto.Directory;
import com.theoaked.daftfiles.dto.UserAgent;
import com.theoaked.daftfiles.factory.UserAgentFactory;
import com.theoaked.daftfiles.service.FileStorageService;

import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

@Controller
@RequestMapping("/")
public class FilesController {

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private UserAgentFactory userAgentFactory;

	public static UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

	@Autowired
	public FilesController() {
	}

	@RequestMapping(value = "/{dir}", method = RequestMethod.GET)
	public String listFiles(@PathVariable("dir") String dir, Model model) {
		try {
			final String reqDir = dir;
			String parentDir = reqDir;
			boolean isParent = false;
			if (parentDir.contains("+")) {
				while (!isParent) {
					if (!parentDir.endsWith("+")) {
						parentDir = parentDir.substring(0, parentDir.length() - 1);
					} else {
						parentDir = parentDir.substring(0, parentDir.length() - 1);
						isParent = true;
					}
				}
			} else {
				parentDir = "//";
			}
			System.out.println("New req: " + dir);
			dir = dir.replace("+", "/");
			File folder = new File("C:\\" + dir);
			System.out.println("Accessing " + folder.getPath());
			File[] listOfFiles = folder.listFiles();
			ArrayList<DaftFile> files = new ArrayList<>();
			ArrayList<Directory> directories = new ArrayList<>();
			if (listOfFiles != null) {
				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()) {
						DaftFile daftFile = new DaftFile();
						daftFile.setNome(listOfFiles[i].getName());
						daftFile.setLink("/downloadFile/" + reqDir + "+" + listOfFiles[i].getName());
						files.add(daftFile);
					} else if (listOfFiles[i].isDirectory()) {
						Directory directory = new Directory();
						directory.setNome(listOfFiles[i].getName());
						directory.setLink(reqDir + "+" + listOfFiles[i].getName());
						directories.add(directory);
					}
				}
			}
			model.addAttribute("parentDir", parentDir);
			model.addAttribute("dirs", directories);
			model.addAttribute("files", files);
			if (dir.endsWith("secret")) {
				return "getOut";
			} else {
				return "listFiles";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "shitGetOut";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/downloadFile/{file}")
	public <ReadableUserAgent> ResponseEntity<Resource> downloadFile(@PathVariable("file") String file,
			HttpServletRequest request, @RequestHeader(value = "User-Agent") String userAgent) {
		file = file.replace("+", "/");

		net.sf.uadetector.ReadableUserAgent agent = parser.parse(userAgent);

		UserAgent usrA = userAgentFactory.factoryTrackingSD(agent.getName(), agent.getType().getName(),
				agent.getVersionNumber().toVersionString(), agent.getOperatingSystem().getName(),
				agent.getOperatingSystem().getProducer(),
				agent.getOperatingSystem().getVersionNumber().toVersionString(), agent.getDeviceCategory().getName());

		System.out.println("New download request (" + usrA.getOs_name() + "): " + file);

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
