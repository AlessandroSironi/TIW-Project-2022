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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/Registry")
@MultipartConfig
public class GetRegistry extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetRegistry() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("user");
		
		UserDAO userDAO = new UserDAO(connection);
		ArrayList<User> users = new ArrayList<User>();
		
		ArrayList<Integer> usersIDInvited = new ArrayList<Integer>();
		
		if (session.getAttribute("invitedUsersID") == null)
			session.setAttribute("invitedUsersID", usersIDInvited);
		else usersIDInvited = (ArrayList<Integer>) session.getAttribute("invitedUsersID");
				
		try {
			users = userDAO.getOtherUsers(currentUser.getID());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
				
		Gson gson = new Gson();
		String json = gson.toJson(users);
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
