package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

public class CreateMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
	public CreateMeeting() {
		super();
	}
	
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
		String title = null;
		Date date = null;
		int duration = -1;
		int capacity = -1;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			title = request.getParameter("title");
			date = (Date) sdf.parse(request.getParameter("date"));
			duration = Integer.parseInt(request.getParameter("duration"));
			capacity = Integer.parseInt(request.getParameter("capacity"));
			
			//TODO: checks
//			if (title == null || title.isEmpty() || dateString == null || dateString.isEmpty() || durationString == null || durationString.isEmpty() ||
//				capacityString == null || capacityString.isEmpty())
//					throw new Exception ("Error: missing or empty values");
//			else {
//				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//				setSubmitDate(date);
//			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or empty values");
			return;
		}
	}

}
