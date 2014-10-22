package ch.cern.alice.alimonalisa.model;

import org.joda.time.*;

/**
 * This class represents the model in this application.
 * @author Vladimir
 *
 */
public class Notification {

	public static enum Category {
		STORAGE, SERVICE, PROXY, NETWORK, INFO, OTHER
	}

	// id of the notification
	private int id;

	// link for more details
	private String link;

	// time when problem occured
	private DateTime startTime;

	// time when problem is solved
	private DateTime endTime;

	// short description for the problem
	private String summary;

	// title of the notification
	private String title;

	// long description for the problem
	private String description;

	// the category of the problem
	private Category category;

	// status of the problem: solved or not solved
	private boolean status;

	// the importance of the problem
	private boolean important;

	// is the notification new
	private boolean isNew;

	public Notification() {
		id = 0;
		link = "";
		startTime = new DateTime();
		endTime = new DateTime();
		summary = "";
		title = "";
		description = "";
		category = null;
		status = false;
		important = false;
		isNew = false;
	}

	public Notification(int id, String link, DateTime startTime, DateTime endTime,
			String summary, String title, String description,
			Category category, boolean status, boolean important, boolean isNew) {
		this.id = id;
		this.link = link;
		this.startTime = startTime;
		this.endTime = endTime;
		this.summary = summary;
		this.title = title;
		this.description = description;
		this.category = category;
		this.status = status;
		this.important = important;
		this.isNew = isNew;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
