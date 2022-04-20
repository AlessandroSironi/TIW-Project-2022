package it.polimi.tiw.controllers;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	TemplateEngine templateEngine;
	
	@Override
	public void init() {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
	}
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
		
		String user = null;
		String password = null;
		String mail = null;
		String name = null;
		String surname = null;
		
		user = request.getParameter("username");
		password = request.getParameter("password");
		mail = request.getParameter("mail");
		name = request.getParameter("name");
		surname = request.getParameter("surname");
		
		try {
			if (user == null || user.isEmpty() || password == null || password.isEmpty() || mail == null || mail.isEmpty() || name == null || name.isEmpty()) {
				throw new Exception ("Missing or empty parameters");
			} else {
			
				try {
					UserDAO userDAO = new UserDAO (ConnectionHandler.getConnection(getServletContext()));
					if (userDAO.checkUserExists(user)) {
						session.setAttribute("signupError", "User already registered");
						templateEngine.process("signup.html", webContext, response.getWriter());
					} else {
						userDAO.registerUser(mail, user, password, name, surname);
						session.removeAttribute("signupError");
						response.sendRedirect("/index.html");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to check credentials");
			return;
		}
	}
}
