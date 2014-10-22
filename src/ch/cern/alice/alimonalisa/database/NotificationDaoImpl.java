package ch.cern.alice.alimonalisa.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import ch.cern.alice.alimonalisa.model.Notification;
import ch.cern.alice.alimonalisa.model.Notification.Category;

import org.joda.time.*;
import org.joda.time.format.*;

public class NotificationDaoImpl implements NotificationDao {

	private SQLiteDatabase db;
	private NotificationDbOpenHelper helper;

	public NotificationDaoImpl(Context context, String lang) {
		helper = new NotificationDbOpenHelper(context, lang);
	}

	@Override
	public void open() throws SQLException {
		db = helper.getWritableDatabase();
	}

	@Override
	public void close() {
		db.close();
		helper.close();
	}

	@Override
	public boolean insert(Notification notification) throws ParseException {

		if (doesExist(notification.getId())) {
			update(notification);
		} else {
			long insertedId = db.insert(NotificationDbOpenHelper.TABLE_NAME,
					null, notificationToContentValues(notification));

			if (insertedId > 0) {
				return true;
			} else
				return false;
		}

		return false;
	}

	public boolean doesExist(int id) throws ParseException {
		List<Notification> allNotifications = getAllNotifications();

		for (Notification n : allNotifications) {
			if (n.getId() == id) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean read(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Notification notification) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Notification> getAllNotifications() throws ParseException {

		List<Notification> allNotifications = new ArrayList<Notification>();

		Cursor c = db.query(NotificationDbOpenHelper.TABLE_NAME, allColumns,
				null, null, null, null, null);

		if (c.moveToFirst()) {
			do {
				allNotifications.add(cursorToNotification(c));
			} while (c.moveToNext());
		}

		return allNotifications;
	}

	@Override
	public ContentValues notificationToContentValues(Notification notification) {
		ContentValues values = new ContentValues();
		
		values.put(NotificationDbOpenHelper.COLUMN_ID, notification.getId());

		values.put(NotificationDbOpenHelper.COLUMN_LINK, notification.getLink());

		values.put(NotificationDbOpenHelper.COLUMN_START_TIME, notification
				.getStartTime().toString());

		values.put(NotificationDbOpenHelper.COLUMN_END_TIME, notification
				.getEndTime().toString());

		values.put(NotificationDbOpenHelper.COLUMN_SUMMARY,
				notification.getSummary());

		values.put(NotificationDbOpenHelper.COLUMN_TITLE,
				notification.getTitle());

		values.put(NotificationDbOpenHelper.COLUMN_DESCRIPTION,
				notification.getDescription());

		// about the category of the notification
		if (notification.getCategory() == Category.STORAGE) {
			values.put(NotificationDbOpenHelper.COLUMN_CATEGORY, 0);
		} else if (notification.getCategory() == Category.SERVICE) {
			values.put(NotificationDbOpenHelper.COLUMN_CATEGORY, 1);
		} else if (notification.getCategory() == Category.PROXY) {
			values.put(NotificationDbOpenHelper.COLUMN_CATEGORY, 2);
		} else if (notification.getCategory() == Category.NETWORK) {
			values.put(NotificationDbOpenHelper.COLUMN_CATEGORY, 3);
		} else if (notification.getCategory() == Category.INFO) {
			values.put(NotificationDbOpenHelper.COLUMN_CATEGORY, 4);
		} else {
			values.put(NotificationDbOpenHelper.COLUMN_CATEGORY, 5);
		}

		// about the status of the notification
		if (notification.getStatus() == false) {
			values.put(NotificationDbOpenHelper.COLUMN_STATUS, 0);
		} else {
			values.put(NotificationDbOpenHelper.COLUMN_STATUS, 1);
		}

		// importance of the notification
		if (notification.isImportant() == false) {
			values.put(NotificationDbOpenHelper.COLUMN_IMPORTANT, 0);
		} else {
			values.put(NotificationDbOpenHelper.COLUMN_IMPORTANT, 1);
		}

		if (notification.isNew()) {
			values.put(NotificationDbOpenHelper.COLUMN_NEW, 0);
		} else {
			values.put(NotificationDbOpenHelper.COLUMN_NEW, 1);
		}

		return values;
	}

	@Override
	public Notification cursorToNotification(Cursor cursor)
			throws ParseException {

		Notification notification = new Notification();

		// to check this later
		DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

		notification.setId(cursor.getInt(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_ID)));

		notification.setLink(cursor.getString(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_LINK)));

		notification
				.setStartTime(formatter.parseDateTime(cursor.getString(cursor
						.getColumnIndex(NotificationDbOpenHelper.COLUMN_START_TIME))));

		notification
				.setEndTime(formatter.parseDateTime(cursor.getString(cursor
						.getColumnIndex(NotificationDbOpenHelper.COLUMN_END_TIME))));

		notification.setSummary(cursor.getString(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_SUMMARY)));

		notification.setTitle(cursor.getString(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_TITLE)));

		notification.setDescription(cursor.getString(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_DESCRIPTION)));

		// for the category
		switch (cursor.getInt(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_CATEGORY))) {
		case 0:
			notification.setCategory(Category.STORAGE);
			break;
		case 1:
			notification.setCategory(Category.SERVICE);
			break;
		case 2:
			notification.setCategory(Category.PROXY);
			break;
		case 3:
			notification.setCategory(Category.NETWORK);
			break;
		case 4:
			notification.setCategory(Category.INFO);
			break;
		case 5:
			notification.setCategory(Category.OTHER);
			break;
		default:
			break;

		}

		// for the status
		switch (cursor.getInt(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_STATUS))) {
		case 0:
			notification.setStatus(false);
			break;
		case 1:
			notification.setStatus(true);
			break;
		default:
			break;
		}

		// for the importance
		switch (cursor.getInt(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_IMPORTANT))) {
		case 0:
			notification.setImportant(false);
			break;
		case 1:
			notification.setImportant(true);
			break;
		default:
			break;
		}

		// does the notification is new
		switch (cursor.getInt(cursor
				.getColumnIndex(NotificationDbOpenHelper.COLUMN_NEW))) {
		case 0:
			notification.setNew(false);
			break;
		case 1:
			notification.setNew(true);
			break;
		default:
			break;
		}

		return notification;
	}
}
