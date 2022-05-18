package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/SignUp")
@MultipartConfig
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	@Override
	public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
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
									response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
									response.getWriter().println("Username already taken.");
								} else {
									userDAO.registerUser(mail, user, password, name, surname);
									User userRegistered = userDAO.checkCredentials(user, password);
									
									Cookie cookieName = new Cookie("name", userRegistered.getName());
									Cookie cookieSurname = new Cookie("surname", userRegistered.getSurname());
									response.addCookie(cookieName);
									response.addCookie(cookieSurname);
									
									request.getSession().setAttribute("user", userRegistered);
									response.setStatus(HttpServletResponse.SC_OK);
									response.setContentType("application/json");
									response.getWriter().println(user);
								}
							} catch (SQLException e) {
								e.printStackTrace();
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							} catch (InvalidKeySpecException e) {
								e.printStackTrace();
							}
						} else {
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							response.getWriter().println("Passwords must be at least 6 characters.");
						}
					} else {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Passwords do not match.");
					}
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Please input a valid mail address.");
				}
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing or empty credentials.");
			e.printStackTrace();
			return;
		}
	}
}
