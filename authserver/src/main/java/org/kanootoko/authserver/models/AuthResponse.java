package org.kanootoko.authserver.models;

import java.io.Serializable;

public class AuthResponse implements Serializable {

	private static final long serialVersionUID = 7429102547619349514L;
	private final String jwt;

	public AuthResponse(String jwt) {
		this.jwt = jwt;
	}

	public String get() {
		return this.jwt;
	}
}