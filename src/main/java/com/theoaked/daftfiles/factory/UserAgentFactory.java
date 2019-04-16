package com.theoaked.daftfiles.factory;

import org.springframework.stereotype.Component;

import com.theoaked.daftfiles.dto.UserAgent;

@Component("userAgentFactory")
public class UserAgentFactory {

	public UserAgent factoryTrackingSD(String browser_name, String browser_type,
			String browser_version, String os_name, String os_producer, String os_version, String device) {
		final UserAgent userAgent = new UserAgent();
		userAgent.setBrowser_name(browser_name);
		userAgent.setBrowser_type(browser_type);
		userAgent.setBrowser_version(browser_version);
		userAgent.setOs_name(os_name);
		userAgent.setOs_producer(os_producer);
		userAgent.setOs_version(os_version);
		userAgent.setDevice(device);
		return userAgent;
	}

}
