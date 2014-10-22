package ch.cern.alice.alimonalisa.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ch.cern.alice.alimonalisa.MainActivity;
import ch.cern.alice.alimonalisa.R;
import ch.cern.alice.alimonalisa.adapters.NotificationAdapter;
import ch.cern.alice.alimonalisa.contentprovider.NotificationContentProvider;
import ch.cern.alice.alimonalisa.database.NotificationDbOpenHelper;
import ch.cern.alice.alimonalisa.model.Notification;
import ch.cern.alice.alimonalisa.model.Notification.Category;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.joda.time.*;
import org.joda.time.format.*;

public class NotificationsFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private ListView listNotifications;
	private NotificationAdapter mAdapter;

	private MainActivity mainActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mainActivity = (MainActivity) activity;
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAdapter = new NotificationAdapter(mainActivity);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.notifications_fragment,
				container, false);

		listNotifications = (ListView) view
				.findViewById(R.id.notificationsList);

		listNotifications.setAdapter(mAdapter);
		listNotifications.setOnItemClickListener(mAdapter);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	public void loadData() {
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		CursorLoader loader = new CursorLoader(mainActivity,
				NotificationContentProvider.CONTENT_URI, null, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		List<Notification> notifications = new ArrayList<Notification>();
		mAdapter.clear();
		if (cursor.moveToFirst()) {
			do {
				Notification notification = new Notification();

				DateTimeFormatter parser = ISODateTimeFormat.dateTime();

				notification.setId(cursor.getInt(cursor
						.getColumnIndex(NotificationDbOpenHelper.COLUMN_ID)));
				
				notification.setLink(cursor.getString(cursor
						.getColumnIndex(NotificationDbOpenHelper.COLUMN_LINK)));
				
				notification
						.setStartTime(parser.parseDateTime(cursor.getString(cursor
								.getColumnIndex(NotificationDbOpenHelper.COLUMN_START_TIME))));
				
				notification
						.setEndTime(parser.parseDateTime(cursor.getString(cursor
								.getColumnIndex(NotificationDbOpenHelper.COLUMN_END_TIME))));
				
				notification
						.setSummary(cursor.getString(cursor
								.getColumnIndex(NotificationDbOpenHelper.COLUMN_SUMMARY)));
				
				notification
						.setTitle(cursor.getString(cursor
								.getColumnIndex(NotificationDbOpenHelper.COLUMN_TITLE)));
				
				notification
						.setDescription(cursor.getString(cursor
								.getColumnIndex(NotificationDbOpenHelper.COLUMN_DESCRIPTION)));
				

				// for the category
				switch (cursor
						.getInt(cursor
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
				switch (cursor
						.getInt(cursor
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
				switch (cursor
						.getInt(cursor
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

				notifications.add(notification);
			} while (cursor.moveToNext());
		}
		mAdapter.addAll(notifications);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.clear();
	}

	public NotificationAdapter getAdapter() {
		return mAdapter;
	}

}
