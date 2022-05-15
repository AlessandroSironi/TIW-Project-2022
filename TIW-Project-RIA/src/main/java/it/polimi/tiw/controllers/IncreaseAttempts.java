package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/IncreaseAttempts")
@MultipartConfig
public class IncreaseAttempts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public IncreaseAttempts() {
		super();
	}

	public void init() throws ServletException {
//		ServletContext servletContext = getServletContext();
//		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
//		templateResolver.setTemplateMode(TemplateMode.HTML);
//		this.templateEngine = new TemplateEngine();
//		this.templateEngine.setTemplateResolver(templateResolver);
//		templateResolver.setSuffix(".html");
//		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		int temp = 1;
		
		if (session.getAttribute("retry") == null)
			session.setAttribute("retry", temp);
		else {
			temp = (int) session.getAttribute("retry");
			temp = temp + 1;
		}
		
		if (temp >= 3) {
			session.removeAttribute("retry");
			session.removeAttribute("invitedUsersID");
			session.removeAttribute("meetingToCreate");
			
			//path = getServletContext().getContextPath() + "/ErrorCreationMeeting";
			//response.sendRedirect(path);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Error: too many attempts.");
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}
}
