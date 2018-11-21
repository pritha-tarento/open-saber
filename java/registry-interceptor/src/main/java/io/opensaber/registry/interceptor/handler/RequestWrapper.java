package io.opensaber.registry.interceptor.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * 
 * @author jyotsna
 *
 */
public class RequestWrapper extends HttpServletRequestWrapper {
	private static Logger logger = LoggerFactory.getLogger(RequestWrapper.class);

	private String body;

	public RequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public String getBody() {
		if (this.body == null || this.body.isEmpty()) {
			BufferedReader bufferedReader = null;
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = null;
			try {
				reader = getReader();
				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}
			} catch (IOException e) {
				logger.error("Can't read from http stream. Set body empty");
			}

			body = buffer.toString();
		}

		return this.body;
	}

	public void setBody(String s) {
		this.body = s;
	}
}