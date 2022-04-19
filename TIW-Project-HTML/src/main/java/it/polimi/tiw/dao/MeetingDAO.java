package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import it.polimi.tiw.beans.Meeting;

public class MeetingDAO {
	private Connection connection;
	
	public MeetingDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void createMeeting(int ID_Creator, String title, Date startDate, Time duration, int capacity) throws SQLException {
		String query = "INSERT INTO Meeting (ID_Creator, title, startDate, duration, capacity) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, ID_Creator);
			pstatement.setString(2, title);
			pstatement.setDate(3, new java.sql.Date(startDate.getTime()));
			pstatement.setTime(4, duration);
			pstatement.setInt(5, capacity);
			pstatement.executeUpdate();
		}
	}
	
	public ArrayList<Meeting> findMeetingsByOwner (int ID_Owner) throws SQLException {
		ArrayList<Meeting> meetings = new ArrayList<>();
		String query = "SELECT title, startDate, duration, capacity FROM Meeting WHERE ID_Creator = ? AND (startDate > ? OR (startDate = ? AND duration >= ?)";
		
		Date date = new Date();
        java.sql.Date date1 = new java.sql.Date(date.getTime());
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, ID_Owner);
			pstatement.setDate(2, date1);
			pstatement.setDate(3, date1);
			pstatement.setTime(4, new Time(System.currentTimeMillis()));
			 try (ResultSet resultSet = pstatement.executeQuery()) {
	                while (resultSet.next()) {
	                	Meeting meet = new Meeting();
	                	meet.setId(resultSet.getInt("ID"));
	                	meet.setId_Creator(resultSet.getInt("ID_Creator"));
	                	meet.setTitle(resultSet.getString("title"));
	                	meet.setStartDate(resultSet.getDate("startDate"));
	                	meet.setDuration(resultSet.getTime("duration"));
	                	meet.setCapacity(resultSet.getInt("capacity"));
	                	
	                	meetings.add(meet);
	                }
			 }
		}
		return meetings;
	}
	
	
}
