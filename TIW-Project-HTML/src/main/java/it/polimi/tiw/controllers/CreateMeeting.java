package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateMeeting")
public class CreateMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
	public CreateMeeting() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
		String path = null;
		
		String title = null;
		Date date = null;
		int duration = -1;
		int capacity = -1;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d'T'HH:mm");
		//2022-04-25T17:26
		
		try {
			title = request.getParameter("title");
			date = (Date) sdf.parse(request.getParameter("date"));
			duration = Integer.parseInt(request.getParameter("duration"));
			capacity = Integer.parseInt(request.getParameter("capacity"));
			
			System.out.println("Parsed Date: " + date.toString());
			
			if (getMeToday().after(date) || date == null) {
				throw new Exception ("Error: the date must be today or later.");
			} else {
				if (title == null || title.isEmpty() || duration < 0 || capacity < 0) {
					throw new Exception ("Error: missing values or bad input");
				}
				else {
					String dateString = date.toString();
					path = getServletContext().getContextPath() + "/Registry?title=" + title + "&date=" + dateString + "&duration=" + duration + "&capacity=" + capacity;
					response.sendRedirect(path);
				}
			}

		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error: missing values or bad input.");
			e.printStackTrace();
			return;
		}
	}
	
	private Date getMeToday() {
		//return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); yesterday
		return new Date(System.currentTimeMillis());
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
