	package ch.cern.alice.alimonalisa.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import ch.cern.alice.alimonalisa.database.NotificationDbOpenHelper;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Content provider class for inserting, querying, updating and deleting
 * Notifications in the NotificationsDb data base table.
 * 
 * @author Vladimir
 * 
 */
public class NotificationContentProvider extends ContentProvider {

	private NotificationDbOpenHelper helper;

	// for all notifications
	private static final int NOTIFICATIONS = 1;

	// for one notification
	private static final int NOTIFICATION_ID = 2;

	// the authority
	public static final String AUTHORITY = "ch.cern.alice.alimonalisa.contentprovider";

	// the table name
	public static final String BASE_PATH = "NotificationsDb";

	// uri of the content
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		// for the table use number 1
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, NOTIFICATIONS);
		// for the id use number 2
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTIFICATION_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int numRowsDeleted = 0;

		switch (sURIMatcher.match(uri)) {
		// for deleting all notifications
		case NOTIFICATIONS:
			numRowsDeleted = db.delete(NotificationDbOpenHelper.TABLE_NAME,
					selection, selectionArgs);
			break;

		// for deleting one notification
		case NOTIFICATION_ID:
			String id = uri.getLastPathSegment();

			if (TextUtils.isEmpty(selection)) {
				numRowsDeleted = db.delete(NotificationDbOpenHelper.TABLE_NAME,
						NotificationDbOpenHelper.COLUMN_ID + "=" + id, null);
			} else {
				numRowsDeleted = db.delete(NotificationDbOpenHelper.TABLE_NAME,
						NotificationDbOpenHelper.COLUMN_ID + "=" + id + " and "
								+ selection, null);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		return numRowsDeleted;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();
		long id = 0;

		switch (sURIMatcher.match(uri)) {
		case NOTIFICATIONS:
			id = db.insert(NotificationDbOpenHelper.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public boolean onCreate() {
		helper = new NotificationDbOpenHelper(getContext(), "en");
		return true;
	}

	/**
	 * (non-Javadoc) This is very important function. I will use this function
	 * for filtering the notifications that the users only wants to see. So,
	 * don't forget about projection, selection, selectionArgs and sortOrder
	 * parameters.
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 *      java.lang.String[], java.lang.String, java.lang.String[],
	 *      java.lang.String)
	 */

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		checkColums(projection);

		builder.setTables(NotificationDbOpenHelper.TABLE_NAME);
		switch (sURIMatcher.match(uri)) {
		// for all notifications
		case NOTIFICATIONS:
			break;
		// for one notification
		case NOTIFICATION_ID:
			builder.appendWhere(NotificationDbOpenHelper.COLUMN_ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = builder.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int numRowsUpdated = 0;

		switch (sURIMatcher.match(uri)) {
		case NOTIFICATIONS:
			numRowsUpdated = db.update(NotificationDbOpenHelper.TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case NOTIFICATION_ID:

			String id = uri.getLastPathSegment();

			if (TextUtils.isEmpty(selection)) {
				numRowsUpdated = db.update(NotificationDbOpenHelper.TABLE_NAME,
						values, NotificationDbOpenHelper.COLUMN_ID + "=" + id,
						selectionArgs);
			} else {
				numRowsUpdated = db.update(NotificationDbOpenHelper.TABLE_NAME,
						values, NotificationDbOpenHelper.COLUMN_ID + "=" + id
								+ " and " + selection, selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return numRowsUpdated;
	}

	private void checkColums(String[] projection) {
		String[] available = { NotificationDbOpenHelper.COLUMN_ID,
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

		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));

			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}
}
