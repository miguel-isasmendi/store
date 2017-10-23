package com.store.architecture.filter.exception;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * This is a wrapper for a common {@link HttpServletResponseWrapper} that
 * returns a particular instance of a writer. This writer is able to copy the
 * contents of the stream wrapped into another one that can be flushed once to
 * review the contents of the body of the response and then be able to modify it
 */
public class HttpServletExceptionResponseCopier extends HttpServletResponseWrapper {

	private PrintWriter writer;

	public HttpServletExceptionResponseCopier(HttpServletResponse response) throws IOException {
		super(response);
	}

	@Override
	public PrintWriter getWriter() throws IOException {

		if (writer == null) {
			writer = new ErrorCopierPrintWriter(getResponse());
		}

		return writer;
	}

}
