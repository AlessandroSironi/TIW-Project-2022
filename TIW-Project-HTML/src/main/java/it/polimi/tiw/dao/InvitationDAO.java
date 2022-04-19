package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

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
		Date date = new Date();
        java.sql.Date date1 = new java.sql.Date(date.getTime());
		
		ArrayList<Meeting> meetings = new ArrayList<>();
		String query = "SELECT M.IDMeeting, M.IDCreator, M.title, M.startDate, M.duration, M.capacity FROM Invitation AS I JOIN Meeting AS M ON I.IDMeeting = M.ID WHERE IDUser = ? AND (M.startDate > ? OR (M.startDate = ? AND M.duration >= ?)";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, IDUser);
			pstatement.setDate(2, date1);
			pstatement.setDate(3, date1);
			pstatement.setTime(4, new Time(System.currentTimeMillis()));
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					Meeting meet = new Meeting();
					meet.setId(result.getInt("ID"));
					meet.setId_Creator(result.getInt("IDCreator"));
					meet.setTitle(result.getString("title"));
					meet.setStartDate(result.getDate("startDate"));
					meet.setDuration(result.getTime("duration"));
					meet.setCapacity(result.getInt("capacity"));
					
					meetings.add(meet);
				}
			}
		}
		
		return meetings;
	}
}
