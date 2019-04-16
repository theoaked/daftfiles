package com.daftmau5.daftfiles.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserAgent {
	@Id
	private Long id;

	private Long tracking_id;

	private String browser_type;

	private String browser_name;

	private String browser_version;

	private String os_name;

	private String os_producer;

	private String os_version;

	private String device;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTracking_id() {
		return tracking_id;
	}

	public void setTracking_id(Long tracking_id) {
		this.tracking_id = tracking_id;
	}

	public String getBrowser_type() {
		return browser_type;
	}

	public void setBrowser_type(String browser_type) {
		this.browser_type = browser_type;
	}

	public String getBrowser_name() {
		return browser_name;
	}

	public void setBrowser_name(String browser_name) {
		this.browser_name = browser_name;
	}

	public String getBrowser_version() {
		return browser_version;
	}

	public void setBrowser_version(String browser_version) {
		this.browser_version = browser_version;
	}

	public String getOs_name() {
		return os_name;
	}

	public void setOs_name(String os_name) {
		this.os_name = os_name;
	}

	public String getOs_producer() {
		return os_producer;
	}

	public void setOs_producer(String os_producer) {
		this.os_producer = os_producer;
	}

	public String getOs_version() {
		return os_version;
	}

	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

}