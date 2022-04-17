package it.polimi.tiw.beans;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class Meeting {
	private int id;
	private int id_creator;
	private String title;
	private Date startDate;
	private Time duration;
	private int capacity;
	private ArrayList<Integer> participants;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId_creator() {
		return id_creator;
	}
	public void setId_creator(int id_creator) {
		this.id_creator = id_creator;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Time getDuration() {
		return duration;
	}
	public void setDuration(Time duration) {
		this.duration = duration;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public ArrayList<Integer> getParticipants() {
		return participants;
	}
	public void setParticipants(ArrayList<Integer> participants) {
		this.participants = participants;
	}
	
	
}
