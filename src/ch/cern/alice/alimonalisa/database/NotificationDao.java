package ch.cern.alice.alimonalisa.database;

import java.text.ParseException;
import java.util.List;

import ch.cern.alice.alimonalisa.model.Notification;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public interface NotificationDao {

	public String[] allColumns = { NotificationDbOpenHelper.COLUMN_ID,
			NotificationDbOpenHelper.COLUMN_LINK,
			NotificationDbOpenHelper.COLUMN_START_TIME,
			NotificationDbOpenHelper.COLUMN_END_TIME,
			NotificationDbOpenHelper.COLUMN_SUMMARY,
			NotificationDbOpenHelper.COLUMN_TITLE,
			NotificationDbOpenHelper.COLUMN_DESCRIPTION,
			NotificationDbOpenHelper.COLUMN_CATEGORY,
			NotificationDbOpenHelper.COLUMN_STATUS,
			NotificationDbOpenHelper.COLUMN_STATUS,
			NotificationDbOpenHelper.COLUMN_IMPORTANT,
			NotificationDbOpenHelper.COLUMN_NEW };

	// function for opening of the database
	public void open() throws SQLException;

	// function for closing of the database
	public void close();

	// function for inserting notification in the database
	public boolean insert(Notification notification) throws ParseException;

	// does the notification with a given id already exist
	public boolean doesExist(int id) throws ParseException;

	// function for reading notification with a given id
	public boolean read(int id);

	// function for updating the database
	public boolean update(Notification notification);

	// function for deleting the notification with a given id
	public boolean delete(int id);

	// function for getting all notifications
	public List<Notification> getAllNotifications() throws ParseException;

	// function for converting Notification object into Content Values object
	public ContentValues notificationToContentValues(Notification notification);

	// notification for converting a Cursor object into Notification object
	public Notification cursorToNotification(Cursor cursor)
			throws ParseException;

}
