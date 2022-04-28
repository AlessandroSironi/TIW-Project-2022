package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;


//TODO: check invitations takes post values from registry.html form and 
	//validate if tentativi <= 3
	// -> validate if #invites <= capacity
	// -> if not, set (in session?) attribute "tentativi" or tentativi++
	// -> if > 3 -> pagina cancellazione con link per home
	//If valid, meetingDAO.registerMeeting() && for (Invitation i : invitations) invitationDAO.registerInvitation()


@WebServlet("/CheckInvitations")
public class CheckInvitations extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public CheckInvitations() {
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		WebContext webContext = new WebContext(request, response, getServletContext(), request.getLocale());
		String path = null;
		
		User userOwner = (User) session.getAttribute("user");
		int userOwnerID = userOwner.getID();
		
		ArrayList<Integer> usersIDInvited = new ArrayList<Integer>();
		
		UserDAO userDAO = new UserDAO(connection);
		MeetingDAO meetingDAO = new MeetingDAO(connection);
		InvitationDAO invitationDAO = new InvitationDAO(connection);
		
		Meeting meetingToCreate = (Meeting) session.getAttribute("meetingToCreate");
		System.out.println("CheckInvitations receinved meeting with title: "+ meetingToCreate.getTitle());
		int capacity = meetingToCreate.getCapacity();
		
		
		//Check that userID values are valid
		String[] invitedUsersStrings = null;
		invitedUsersStrings = request.getParameterValues("usersInvited");

		if (invitedUsersStrings == null) {
			System.out.println("invitedUsersString == null");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}

		for (String s : invitedUsersStrings) {
			System.out.println("Parsing ID: "+ s);
			Integer id = Integer.parseInt(s);		
			try {
				if (!userDAO.checkUserIDExists(id)) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			usersIDInvited.add(id);
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
				
				path = getServletContext().getContextPath() + "/ErrorCreationMeeting";
				response.sendRedirect(path);
				//send error to Error Page
			} else {
				session.setAttribute("retry", temp);
				System.out.println("retry: " + session.getAttribute("retry"));
				session.setAttribute("invitedUsersID", usersIDInvited);
				path = getServletContext().getContextPath() + "/Registry";
				
				
				int tooMany = usersIDInvited.size() - meetingToCreate.getCapacity();
				String tooManyString = "Too many users selected. Please, deselect at least " + tooMany + " invitations.";
			
				session.setAttribute("attemptsErrorMsg", tooManyString);
				response.sendRedirect(path);
				
				
				// send error to registry with selected items.
			}
		} else { //# of people invited is ok!
			try {
				if (meetingDAO.getMeetingID(userOwnerID, meetingToCreate.getTitle(), meetingToCreate.getStartDate(), meetingToCreate.getDuration(), meetingToCreate.getCapacity()) != -1) {
					path = getServletContext().getContextPath() + "/Home"; 
					webContext.setVariable("attemptsErrorMsg", "Meeting already exists.");
					templateEngine.process(path,  webContext, response.getWriter());
				} else {
					connection.setAutoCommit(false);
					meetingDAO.createMeeting(userOwnerID, meetingToCreate.getTitle(), meetingToCreate.getStartDate(), meetingToCreate.getDuration(), meetingToCreate.getCapacity());
					int meetingID = meetingDAO.getMeetingID(userOwnerID, meetingToCreate.getTitle(), meetingToCreate.getStartDate(), meetingToCreate.getDuration(), meetingToCreate.getCapacity());
					System.out.println("meetingID: "+ meetingID);
					for (int u : usersIDInvited) {
						invitationDAO.inviteUser(meetingID, u);
					}
					connection.commit();
					connection.setAutoCommit(true);
					
					path = getServletContext().getContextPath() + "/Home";
					
				}
				session.removeAttribute("retry");
				session.removeAttribute("invitedUsersID");
				session.removeAttribute("attemptsErrorMsg");
				response.sendRedirect(path);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					connection.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in creating the meeting.");
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