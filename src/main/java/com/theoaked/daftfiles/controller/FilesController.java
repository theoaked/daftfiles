package com.theoaked.daftfiles.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.theoaked.daftfiles.DaftfilesApplication;
import com.theoaked.daftfiles.dto.DaftFile;
import com.theoaked.daftfiles.dto.Directory;
import com.theoaked.daftfiles.dto.User;
import com.theoaked.daftfiles.dto.UserAgent;
import com.theoaked.daftfiles.factory.UserAgentFactory;
import com.theoaked.daftfiles.service.FileStorageService;

import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

@Controller
@RequestMapping("/")
public class FilesController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private UserAgentFactory userAgentFactory;

	@Autowired
	private DaftfilesApplication ap;

	public static UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

	@Autowired
	public FilesController() {
	}

	@RequestMapping(value = "/{dir}", method = RequestMethod.GET)
	public String listFiles(@PathVariable("dir") String dir, Model model, HttpServletResponse response,
			HttpServletRequest request, @RequestHeader(value = "User-Agent") String userAgent) {

		// get the user's IP address
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// get the user-agent data from http-request
		net.sf.uadetector.ReadableUserAgent agent = parser.parse(userAgent);
		// merge user-agent data into an object
		UserAgent usrA = userAgentFactory.factoryTrackingSD(agent.getName(), agent.getType().getName(),
				agent.getVersionNumber().toVersionString(), agent.getOperatingSystem().getName(),
				agent.getOperatingSystem().getProducer(),
				agent.getOperatingSystem().getVersionNumber().toVersionString(), agent.getDeviceCategory().getName());

		// get the actual session for login attributes validation
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		try {
			if (user == null) {
				logger.warn("[NAVIGATION][" + ip + "] Invalid authentication.");
				response.sendRedirect("/login");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[NAVIGATION][" + ip + "] Could not authenticate - " + e.getMessage());
		}
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
				parentDir = "+";
			}
			dir = dir.replace("+", "/");
			File folder = new File("/" + dir);
			logger.info("[NAVIGATION][" + ip + "]" + folder.getPath() + " - " + usrA.getOs_name() + "_"
					+ usrA.getBrowser_name());
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
			logger.error("[NAVIGATION][" + ip + "] Error - " + e.getMessage() + " - " + usrA.getOs_name() + "_"
					+ usrA.getBrowser_name());
			return "shitGetOut";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/downloadFile/{file}")
	public <ReadableUserAgent> ResponseEntity<Resource> downloadFile(@PathVariable("file") String file,
			HttpServletRequest request, @RequestHeader(value = "User-Agent") String userAgent,
			HttpServletResponse response) {

		// get the user's IP address
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		try {
			if (user == null) {
				logger.warn("[DOWNLOAD][" + ip + "] Invalid authentication.");
				response.sendRedirect("/login");
			}
		} catch (Exception e) {
			logger.warn("[DOWNLOAD][" + ip + "] Could not authenticate - " + e.getMessage());
		}
		file = file.replace("+", "/");

		net.sf.uadetector.ReadableUserAgent agent = parser.parse(userAgent);

		UserAgent usrA = userAgentFactory.factoryTrackingSD(agent.getName(), agent.getType().getName(),
				agent.getVersionNumber().toVersionString(), agent.getOperatingSystem().getName(),
				agent.getOperatingSystem().getProducer(),
				agent.getOperatingSystem().getVersionNumber().toVersionString(), agent.getDeviceCategory().getName());

		logger.info("[DOWNLOAD][" + ip + "] " + file + " - " + usrA.getOs_name() + "_" + usrA.getBrowser_name());

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
			logger.info("[DOWNLOAD][" + ip + "] Error - " + ex.getMessage() + " - " + usrA.getOs_name() + "_"
					+ usrA.getBrowser_name());
			return (ResponseEntity<Resource>) ResponseEntity.badRequest();
		}
	}

	@RequestMapping("/authenticate")
	protected void authenticate(HttpServletRequest request, HttpServletResponse response) {
		String cmd = request.getParameter("cmd");
		String login = request.getParameter("login");
		String senha = request.getParameter("senha");

		// get the user's IP address
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}

		try {
			if ("login".equals(cmd)) {
				HttpSession session = request.getSession();
				if (ap.checkLogin(login, senha) == null) {
					response.sendRedirect("/login");
				} else {
					session.setAttribute("user", ap.checkLogin(login, senha));
					User u = (User) session.getAttribute("user");
					logger.info("[LOGIN][" + ip + "] New login - " + u.getLogin());
					response.sendRedirect("/+");
				}
			}
		} catch (Exception e) {
			logger.info("[LOGIN] Could not login - " + e.getMessage());
		}
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}
}
