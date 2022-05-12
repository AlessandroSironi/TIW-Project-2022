package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.InvitationDAO;
import it.polimi.tiw.dao.MeetingDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/Home")
@MultipartConfig
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToHomePage() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		Cookie cookieName = new Cookie("name", user.getName());
		Cookie cookieSurname = new Cookie("surname", user.getSurname());
		response.addCookie(cookieName);
		response.addCookie(cookieSurname);
		
		MeetingDAO meetingDAO = new MeetingDAO(connection);
		ArrayList<Meeting> meetingsCreated = new ArrayList<Meeting>();
		
		InvitationDAO invitationDAO = new InvitationDAO(connection);
		ArrayList<Meeting> meetingsInvited = new ArrayList<Meeting>();

		try {
			meetingsCreated = meetingDAO.findMeetingsByOwner(user.getID());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Encountered an error while loading user's meetings.");
			return;
		}
		
		try {
			meetingsInvited = invitationDAO.findMeetingsByInvitation(user.getID());
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Encountered an error while loading meetings to which the user was invited to.");
			return;
		}
		
		//Remove attributes relating to failed meeting creations, if user goes back to home page at any point
		session.removeAttribute("retry");
		session.removeAttribute("invitedUsersID");
		session.removeAttribute("attemptsErrorMsg");
		session.removeAttribute("meetingToCreate");

		// Redirect to the Home page and add missions to the parameters
		String path = "/WEB-INF/home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("meetingsCreated", meetingsCreated);
		ctx.setVariable("meetingsInvited", meetingsInvited);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
