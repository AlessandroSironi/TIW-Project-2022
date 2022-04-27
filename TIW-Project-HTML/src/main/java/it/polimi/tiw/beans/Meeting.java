package it.polimi.tiw.beans;

import java.sql.Timestamp;

public class Meeting {
	private int id;
	private int id_Creator;
	private String title;
	private Timestamp startDate;
	private int duration;
	private int capacity;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId_Creator() {
		return id_Creator;
	}
	public void setId_Creator(int id_creator) {
		this.id_Creator = id_creator;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Timestamp getStartDate() {
		return startDate;
	}
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
}
