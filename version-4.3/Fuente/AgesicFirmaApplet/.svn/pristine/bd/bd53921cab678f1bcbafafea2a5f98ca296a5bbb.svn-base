package uy.gub.agesic.firma.cliente.ws.trust.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * 
 * @author fsierra
 *
 */
public class ByteArrayDataSource implements DataSource {
	
	private byte[] bytes;

	private String contentType;

	public ByteArrayDataSource(byte[] bytes, String contentType) {
		super();
		this.bytes = bytes;
		this.contentType = contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		final ByteArrayDataSource bads = this;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FilterOutputStream a = new FilterOutputStream(baos);
		baos.close();
		bads.setBytes(baos.toByteArray());
		return a;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}

}
