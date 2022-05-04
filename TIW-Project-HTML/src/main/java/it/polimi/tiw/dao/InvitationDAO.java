package it.polimi.tiw.dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.Timestamp;
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
		
		String query = "SELECT M.ID, M.ID_Creator, M.title, M.startDate, M.duration, M.capacity FROM Invitation AS I JOIN Meeting AS M ON I.IDMeeting = M.ID WHERE I.IDUser = ? AND M.startDate > ? ORDER BY M.startDate";
		String query1 = "SELECT user FROM User WHERE ID = ?";
				
		try(PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, IDUser);
			pstatement.setTimestamp(2, new java.sql.Timestamp(date.getTime()));
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Meeting meet = new Meeting();
					meet.setId(result.getInt("ID"));
					meet.setId_Creator(result.getInt("ID_Creator"));
					meet.setTitle(result.getString("title"));
					meet.setStartDate(result.getTimestamp("startDate"));
					meet.setDuration(result.getInt("duration"));
					meet.setCapacity(result.getInt("capacity"));
					
					try (PreparedStatement pstatement1 = connection.prepareStatement(query1);) {
                		pstatement1.setInt(1, meet.getId_Creator());
                		try (ResultSet resultSet1 = pstatement1.executeQuery()) {
                			if (resultSet1.next()) {
                				meet.setUser_Creator(resultSet1.getString("user"));
                			}
                		} catch (SQLException e) {
                    		e.printStackTrace();
                    	}
                	} catch (SQLException e) {
                		e.printStackTrace();
                	}
					meetings.add(meet);
				}
			}
		}
		return meetings;
	}
	
}
