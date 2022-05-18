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

import it.polimi.tiw.beans.Meeting;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.InvitationDAO;
import it.polimi.tiw.dao.MeetingDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CheckInvitations")
@MultipartConfig
public class CheckInvitations extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckInvitations() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		User userOwner = (User) session.getAttribute("user");
		int userOwnerID = userOwner.getID();
		
		ArrayList<Integer> usersIDInvited = new ArrayList<Integer>();
		
		UserDAO userDAO = new UserDAO(connection);
		MeetingDAO meetingDAO = new MeetingDAO(connection);
		InvitationDAO invitationDAO = new InvitationDAO(connection);
		
		Meeting meetingToCreate = (Meeting) session.getAttribute("meetingToCreate");
		
		int capacity = meetingToCreate.getCapacity();
		
		
		//Check that userID values are valid
		String[] invitedUsersStrings = null;
		invitedUsersStrings = request.getParameterValues("usersInvited");

		if (invitedUsersStrings == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Missing parameters.");
			return;
		}

		for (String s : invitedUsersStrings) {
			try {
				Integer id = Integer.parseInt(s);
				try {
					if (!userDAO.checkUserIDExists(id)) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().write("Invalid parameters.");
						return;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				usersIDInvited.add(id);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("Invalid parameters.");
				return;
			}
		}
		
		int temp = 1;
		if (usersIDInvited.size() > capacity) {
			if (session.getAttribute("retry") == null)
				session.setAttribute("retry", temp);
			else {
				temp = (int) session.getAttribute("retry");
				temp = temp + 1;
			}
			if (temp >= 3) {
				session.removeAttribute("retry");
				session.removeAttribute("invitedUsersID");
				
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("Error: too many attempts.");
				//send error to Error Page
			} else {
				session.setAttribute("retry", temp);
				session.setAttribute("invitedUsersID", usersIDInvited);
				
				int tooMany = usersIDInvited.size() - meetingToCreate.getCapacity();
				String tooManyString = "Too many users selected. Please, deselect at least " + tooMany + " invitations.";
			
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write(tooManyString);
				
				
				
				// send error to registry with selected items.
			}
		} else { //# of people invited is ok!
			try {
				if (meetingDAO.getMeetingID(userOwnerID, meetingToCreate.getTitle(), meetingToCreate.getStartDate(), meetingToCreate.getDuration(), meetingToCreate.getCapacity()) != -1) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().write("Meeting already exists.");
				} else {
					connection.setAutoCommit(false);
					meetingDAO.createMeeting(userOwnerID, meetingToCreate.getTitle(), meetingToCreate.getStartDate(), meetingToCreate.getDuration(), meetingToCreate.getCapacity());
					int meetingID = meetingDAO.getMeetingID(userOwnerID, meetingToCreate.getTitle(), meetingToCreate.getStartDate(), meetingToCreate.getDuration(), meetingToCreate.getCapacity());
					
					for (int u : usersIDInvited) {
						invitationDAO.inviteUser(meetingID, u);
					}
					connection.commit();
					connection.setAutoCommit(true);					
				}
				session.removeAttribute("retry");
				session.removeAttribute("invitedUsersID");
				session.removeAttribute("attemptsErrorMsg");
				
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().write("Error in creating the meeting.");
			}
		}
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}