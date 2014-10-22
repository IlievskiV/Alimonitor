package ch.cern.alice.alimonalisa.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotificationDbOpenHelper extends SQLiteOpenHelper {

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_START_TIME = "start_time";
	public static final String COLUMN_END_TIME = "end_time";
	public static final String COLUMN_SUMMARY = "summary";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_IMPORTANT = "important";
	public static final String COLUMN_NEW = "new";
	public static final String TABLE_NAME = "NotificationsDb";

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME_EXPRESSION = "AddressBookDatabase_%s.db";

	public static String DATABASE_CREATE = String
			.format("create table %s (%s integer primary key, %s text, %s datetime not null, "
					+ "%s datetime, %s text, %s text not null, %s text, %s text not null, %s integer default 0 not null, %s integer default 0 not null, %s integer default 1 not null)",
					TABLE_NAME, COLUMN_ID, COLUMN_LINK, COLUMN_START_TIME,
					COLUMN_END_TIME, COLUMN_SUMMARY, COLUMN_TITLE,
					COLUMN_DESCRIPTION, COLUMN_CATEGORY, COLUMN_STATUS,
					COLUMN_IMPORTANT, COLUMN_NEW);

	public NotificationDbOpenHelper(Context context, String lang) {
		super(context, String.format(DATABASE_NAME_EXPRESSION, lang), null,
				DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
		onCreate(db);

	}

}
