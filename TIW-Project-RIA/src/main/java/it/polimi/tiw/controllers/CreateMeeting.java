package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateMeeting")
@MultipartConfig
public class CreateMeeting extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public CreateMeeting() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
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
			
			if (getMeToday().after(date) || date == null) {
				throw new Exception ("Error: the date must be today or later.");
			} else {
				if (title == null || title.isEmpty() || duration <= 0 || capacity <= 0) {
					throw new Exception ("Error: missing values or bad input");
				}
				else {				
					Meeting meetingToCreate = new Meeting();
					meetingToCreate.setTitle(title);
					meetingToCreate.setStartDate(new Timestamp(date.getTime()));
					meetingToCreate.setDuration(duration);
					meetingToCreate.setCapacity(capacity);
					
					session.setAttribute("meetingToCreate", meetingToCreate);
					
					Gson gson = new Gson();
					String json = gson.toJson(meetingToCreate);
					
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        response.getWriter().write(json);
				}
			}

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace();
			return;
		}
	}
	
	private Date getMeToday() {
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
