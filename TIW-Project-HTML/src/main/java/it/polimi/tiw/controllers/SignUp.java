package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	@Override
	public void init() throws ServletException {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
		
		String path = null;
		
		String user = null;
		String password = null;
		String passwordRepeat = null;
		String mail = null;
		String name = null;
		String surname = null;
		
		user = request.getParameter("username");
		password = request.getParameter("password");
		passwordRepeat = request.getParameter("passwordRepeat");
		mail = request.getParameter("mail");
		name = request.getParameter("name");
		surname = request.getParameter("surname");

		
		//TODO: check mail if input correctly.
		//TODO: check if password password1 is the same.
		
		try {
			if (user == null || user.isEmpty() || password == null || password.isEmpty() || passwordRepeat == null || passwordRepeat.isEmpty() ||
				mail == null || mail.isEmpty() || name == null || name.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			} else {
				Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
		        Matcher mat = pattern.matcher(mail);
		        if (mat.matches()) { //Check if mail address is in the correct form.
					if (password.equals(passwordRepeat)) { //Check if password and passwordRepeat match.
						if(password.length() >= 6) {
							try {
								UserDAO userDAO = new UserDAO (connection);
								if (userDAO.checkUserExists(user)) { // Check that username is not already taken.
									webContext.setVariable("signUpErrorMsg", "Username already taken.");
									path = "/index.html";
									templateEngine.process(path,  webContext, response.getWriter());
								} else {
									userDAO.registerUser(mail, user, password, name, surname);
									
									webContext.setVariable("signUpOKMsg", "User has been registered. Please login.");
									path = "/index.html";
									templateEngine.process(path,  webContext, response.getWriter());
								}
							} catch (SQLException e) {
								e.printStackTrace();
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							} catch (InvalidKeySpecException e) {
								e.printStackTrace();
							}
						} else {
							webContext.setVariable("signUpErrorMsg", "Passwords must be at least 6 characters.");
							path = "/index.html";
							templateEngine.process(path,  webContext, response.getWriter());
						}
					} else {
						webContext.setVariable("signUpErrorMsg", "Passwords do not match.");
						path = "/index.html";
						templateEngine.process(path,  webContext, response.getWriter());
					}
				} else {
					webContext.setVariable("signUpErrorMsg", "Please input a valid mail address.");
					path = "/index.html";
					templateEngine.process(path,  webContext, response.getWriter());
				}
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing or empty credential values.");
			e.printStackTrace();
			return;
		}
	}
}
