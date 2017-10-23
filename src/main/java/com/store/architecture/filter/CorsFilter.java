package com.store.architecture.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import lombok.NonNull;

public class CorsFilter implements Filter {
	private static final Logger logger = Logger.getLogger(CorsFilter.class.getName());

	private String allowedOrigin;

	@Inject
	public CorsFilter(@NonNull String allowedOrigin) {
		this.allowedOrigin = allowedOrigin;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String clientServerName = httpRequest.getServerName();
		logger.info("Evaluating CORS header addition for client: " + clientServerName + " ...");

		if (StringUtils.equals(clientServerName, allowedOrigin)) {

			filterChain.doFilter(request, response);

			httpResponse.addHeader("Access-Control-Allow-Origin", allowedOrigin);
			logger.info("CORS header successfully included in response!");
		} else {
			logger.info("CORS header not included in response!");
		}
	}

	@Override
	public void destroy() {
	}

}
