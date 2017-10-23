package com.store.architecture.filter.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.SystemService;
import com.google.gson.Gson;

public class CustomExceptionFilter implements Filter {
	private Gson gson;

	@Inject
	public CustomExceptionFilter(Gson gson) {
		this.gson = gson;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	private void setResponse(HttpServletResponse httpServletResponse, ExceptionResponseData errorData)
			throws IOException {
		httpServletResponse.setStatus(errorData.getCode());
		httpServletResponse.getOutputStream().print(gson.toJson(errorData));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setContentType(SystemService.MIME_JSON);

		try {
			HttpServletExceptionResponseCopier responseCopier = new HttpServletExceptionResponseCopier(
					httpServletResponse);
			filterChain.doFilter(request, responseCopier);

			ErrorCopierPrintWriter errorCopierPrintWriter = (ErrorCopierPrintWriter) responseCopier.getWriter();

			// This is here to cover the case in which an exception has been catched by
			// Guice and been converted to a String in the body
			if (errorCopierPrintWriter.hasErrorContent()) {

				errorCopierPrintWriter.getErrorContentsWriter().flush();
				String originalExceptionBodyString = new String(
						errorCopierPrintWriter.getCopyOutputStream().toByteArray(), response.getCharacterEncoding());

				GuiceExceptionResponseDataWrapper originalExceptionData = this.gson
						.fromJson(originalExceptionBodyString, GuiceExceptionResponseDataWrapper.class);

				responseCopier.resetBuffer();

				setResponse(httpServletResponse, new ExceptionResponseDataBuilder(originalExceptionData).build());
			} else {
				errorCopierPrintWriter.flush();
			}
		} catch (ServletException e) {
			// This is here to cover the case in which an exception has been thrown in a
			// filter execution
			if (ServiceException.class.isAssignableFrom(e.getCause().getClass())) {
				// We cover the case for Guice compatible exceptions
				ServiceException originalException = (ServiceException) e.getCause();

				setResponse(httpServletResponse, new ExceptionResponseDataBuilder(originalException).build());
			} else {
				// We cover the case for natives exceptions being thrown
				setResponse(httpServletResponse,
						new ExceptionResponseDataBuilder(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getCause())
								.build());
			}
		} catch (IllegalArgumentException e) {
			// This is here to cover the case in which an exception has been thrown in a
			// filter execution
			setResponse(httpServletResponse,
					new ExceptionResponseDataBuilder(HttpServletResponse.SC_BAD_REQUEST, e).build());
		} catch (Throwable e) {
			// This is here to cover the case in which an exception has been thrown in a
			// filter execution
			setResponse(httpServletResponse,
					new ExceptionResponseDataBuilder(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e).build());
		}
	}

	@Override
	public void destroy() {
	}

	private static class GuiceExceptionResponseDataWrapper {
		private GuiceExceptionResponseData error;

		public GuiceExceptionResponseData getError() {
			return error;
		}
	}

	private static class GuiceExceptionResponseData {
		private ArrayList<GuiceExceptionResponseInnerData> errors;
		private int code;
		private String message;

		public int getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

		@SuppressWarnings("unused")
		public ArrayList<GuiceExceptionResponseInnerData> getErrors() {
			return errors;
		}

	}

	@SuppressWarnings("unused")
	private static class GuiceExceptionResponseInnerData {
		private String domain;
		private String reason;
		private String message;
	}

	private static class ExceptionResponseData {
		private int code;
		private String message;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		@SuppressWarnings("unused")
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	private static class ExceptionResponseDataBuilder {
		private static Pattern EXCEPTION_MESSAGE_PATTERN = Pattern.compile("^(([^:]*):\\s)?([\\W\\w.]*)$");
		private int code;
		private String message;

		private ExceptionResponseDataBuilder(int code) {
			this.setCode(code);
		}

		public ExceptionResponseDataBuilder(GuiceExceptionResponseDataWrapper guiceExceptionWrapper) {
			this(guiceExceptionWrapper.getError().getCode());

			Matcher messageMatcher = EXCEPTION_MESSAGE_PATTERN.matcher(guiceExceptionWrapper.getError().getMessage());

			if (messageMatcher.matches()) {
				this.setMessage(messageMatcher.group(messageMatcher.groupCount()));
			}

		}

		public ExceptionResponseDataBuilder(int code, Throwable exception) {
			this(code);
			this.setMessage(exception.getMessage());
		}

		public ExceptionResponseDataBuilder(ServiceException exception) {
			this(exception.getStatusCode());
			this.setMessage(exception.getMessage());
		}

		public void setCode(int code) {
			this.code = code;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public ExceptionResponseData build() {
			ExceptionResponseData response = new ExceptionResponseData();

			response.setCode(this.code);
			response.setMessage(this.message);
			// TODO https://cactus.atlassian.net/browse/CAC-333 Check how to retrieve this
			// value from the log context
			// response.setUuid(ApiProxy.getCurrentEnvironment().getAttributes().get("com.google.appengine.runtime.request_log_id").toString());

			return response;
		}
	}

}
