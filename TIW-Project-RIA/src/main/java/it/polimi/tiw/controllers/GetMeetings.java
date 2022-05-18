package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.InvitationDAO;
import it.polimi.tiw.dao.MeetingDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetMeetings")
@MultipartConfig
public class GetMeetings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetMeetings() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
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
		
		// Write json on the response
		Gson gson = new GsonBuilder().create();
		String jsonMeetingsCreated = gson.toJson(meetingsCreated);
		String jsonMeetingsInvited = gson.toJson(meetingsInvited);
		
		String bothJson = "[" + jsonMeetingsCreated + "," + jsonMeetingsInvited + "]";
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		response.getWriter().write(bothJson);
		
		//Remove attributes relating to failed meeting creations, if user goes back to home page at any point
		session.removeAttribute("retry");
		session.removeAttribute("invitedUsersID");
		session.removeAttribute("meetingToCreate");
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
