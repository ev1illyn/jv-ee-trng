package org.e.store.loja.security;

import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.e.store.loja.daos.SecurityDao;
import org.e.store.loja.models.SystemUser;

@Model
public class CurrentUser {

	@Inject
	private HttpServletRequest request;
	
	@Inject
	private SecurityDao securityDao;
	
	private SystemUser systemUser;
	
	@PostConstruct
	public void loadSystemUser() {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			String email = request.getUserPrincipal().getName();
			systemUser = securityDao.findByEmail(email);
		}
	}
	
	public SystemUser get() {
		return systemUser;
	}
	
}
