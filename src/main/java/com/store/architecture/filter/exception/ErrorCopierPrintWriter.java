package com.store.architecture.filter.exception;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.api.server.spi.EndpointsServlet;

/**
 * This class processes the strings received from the Guice execution flow and
 * detects if there's an error following the format of
 * {@link RestResponseResultWriter#writeError(com.google.api.server.spi.ServiceException)}.
 * In case of receiving the exception string thrown by
 * {@link EndpointsServlet#service(javax.servlet.http.HttpServletRequest, HttpServletResponse)},
 * it takes it and reformat to the format expected for
 * {@link RestResponseResultWriter#writeError(com.google.api.server.spi.ServiceException)}.
 *
 */
public class ErrorCopierPrintWriter extends PrintWriter {
	private static final Pattern ERROR_PATTERN = Pattern.compile("^\\{\\n\\s\"error\":[\\w\\W]*\\}$");
	private static final String NOT_FOUND_ERROR_PATTERN = "Not Found";

	private OutputStreamWriter errorContentsWriter;
	private String errorStringEncoding;
	private boolean hasErrorContent = false;
	private ByteArrayOutputStream outputStream;

	public ErrorCopierPrintWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	public ErrorCopierPrintWriter(ServletResponse response) throws IOException {
		super(new OutputStreamWriter(response.getOutputStream(), response.getCharacterEncoding()));
		this.errorStringEncoding = response.getCharacterEncoding();
	}

	public void write(String s, int off, int len) {
		try {
			synchronized (this.lock) {
				// This is a copy of the implementation of the method #ensureOpen
				if (this.out == null)
					throw new IOException("Stream closed");

				// This is a copy of super.write
				this.out.write(s, off, len);

				// Starting custom String processing
				String stringReceived = s;

				Matcher matcher = ERROR_PATTERN.matcher(stringReceived);

				this.hasErrorContent = matcher.matches();

				// This cover an special case of error generation (see
				// com.google.api.server.spi.EndpointsServlet#service(HttpServletRequest
				// request, HttpServletResponse response)) where Guice returns a straight out
				// hardcoded String...
				if (!this.hasErrorContent && NOT_FOUND_ERROR_PATTERN.equals(stringReceived)) {
					stringReceived = "{\"error\":{\"code\":" + HttpServletResponse.SC_NOT_FOUND
							+ ", \"message\":\"com.google.api.server.spi.response.NotFoundException: " + stringReceived
							+ "\"}}";
					this.hasErrorContent = true;
				}

				if (this.hasErrorContent) {
					this.getErrorContentsWriter().write(stringReceived, 0, stringReceived.length());
				}
			}
		} catch (InterruptedIOException x) {
			Thread.currentThread().interrupt();
		} catch (IOException x) {
			try {
				FieldUtils.writeField(this, "trouble", Boolean.TRUE, true);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	synchronized public OutputStreamWriter getErrorContentsWriter() {
		if (this.errorContentsWriter == null) {
			this.outputStream = new ByteArrayOutputStream();
			try {
				this.errorContentsWriter = new OutputStreamWriter(this.outputStream, this.errorStringEncoding);
			} catch (UnsupportedEncodingException e) {
				this.errorContentsWriter = new OutputStreamWriter(this.outputStream);
				this.errorStringEncoding = this.errorContentsWriter.getEncoding();
			}
		}
		return errorContentsWriter;
	}

	public boolean hasErrorContent() {
		return hasErrorContent;
	}

	public ByteArrayOutputStream getCopyOutputStream() {
		return this.outputStream;
	}
}