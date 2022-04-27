package it.polimi.tiw.dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Meeting;

public class InvitationDAO {
private Connection connection;
	
	public InvitationDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void inviteUser(int idMeeting, int IDUserToInvite) throws SQLException {
		String query = "INSERT INTO Invitation (IDMeeting, IDUser) VALUES (?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idMeeting);
			pstatement.setInt(2, IDUserToInvite);
			pstatement.executeUpdate();
		}
	}
	
	public ArrayList<Meeting> findMeetingsByInvitation (int IDUser) throws SQLException {
		Date date = new Date(System.currentTimeMillis());
		
		ArrayList<Meeting> meetings = new ArrayList<>();
		String query = "SELECT M.ID, M.ID_Creator, M.title, M.startDate, M.duration, M.capacity FROM Invitation AS I JOIN Meeting AS M ON I.IDMeeting = M.ID WHERE I.IDUser = ? AND M.startDate > ?";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, IDUser);
			pstatement.setDate(2, new java.sql.Date(date.getTime()));
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					Meeting meet = new Meeting();
					meet.setId(result.getInt("ID"));
					meet.setId_Creator(result.getInt("ID_Creator"));
					meet.setTitle(result.getString("title"));
					meet.setStartDate(result.getDate("startDate"));
					meet.setDuration(result.getInt("duration"));
					meet.setCapacity(result.getInt("capacity"));
					
					meetings.add(meet);
				}
			}
		}
		
		return meetings;
	}
}
