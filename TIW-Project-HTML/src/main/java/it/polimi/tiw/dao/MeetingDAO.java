package it.polimi.tiw.dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

import it.polimi.tiw.beans.Meeting;

public class MeetingDAO {
	private Connection connection;
	
	public MeetingDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void createMeeting(int ID_Creator, String title, Date startDate, int duration, int capacity) throws SQLException {
		String query = "INSERT INTO Meeting (ID_Creator, title, startDate, duration, capacity) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, ID_Creator);
			pstatement.setString(2, title);
			pstatement.setDate(3, new java.sql.Date(startDate.getTime()));
			pstatement.setInt(4, duration);
			pstatement.setInt(5, capacity);
			pstatement.executeUpdate();
		}
	}
	
	public ArrayList<Meeting> findMeetingsByOwner (int ID_Owner) throws SQLException {
		ArrayList<Meeting> meetings = new ArrayList<>();
		String query = "SELECT ID, ID_Creator, title, startDate, duration, capacity FROM Meeting WHERE ID_Creator = ? AND startDate > ?";
		
		Date date = new Date(System.currentTimeMillis());
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, ID_Owner);
			pstatement.setDate(2, new java.sql.Date(date.getTime()));
			
			try (ResultSet resultSet = pstatement.executeQuery()) {
	               while (resultSet.next()) {
	            	   Meeting meet = new Meeting();
	                	meet.setId(resultSet.getInt("ID"));
	                	meet.setId_Creator(resultSet.getInt("ID_Creator"));
	                	meet.setTitle(resultSet.getString("title"));
	                	meet.setStartDate(resultSet.getDate("startDate"));
	                	meet.setDuration(resultSet.getInt("duration"));
	                	meet.setCapacity(resultSet.getInt("capacity"));
	                	
	                	meetings.add(meet);
	                }
			 }
		}
		return meetings;
	}
	
	public int getMeetingID (int ID_Creator, String title, Date startDate, int duration, int capacity) throws SQLException {
		String query = "SELECT ID FROM Meeting WHERE ID_Creator = ? AND title = ? AND startDate = ? AND duration = ? AND capacity = ?";
		int meetID = -1;
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, ID_Creator);
			pstatement.setString(2, title);
			pstatement.setDate(3,  new java.sql.Date(startDate.getTime()));
			pstatement.setInt(4, duration);
			pstatement.setInt(5, capacity);
			
			try (ResultSet resultSet = pstatement.executeQuery()) {
				if (resultSet.next())
					meetID = resultSet.getInt("ID");
			}
		}
		return meetID;
	}
	
}
