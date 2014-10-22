package ch.cern.alice.alimonalisa.services;

import com.google.android.gms.drive.internal.ac;

import ch.cern.alice.alimonalisa.CommonConstants;
import ch.cern.alice.alimonalisa.NotificationDetailsActivity;
import ch.cern.alice.alimonalisa.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * This class creates pushing notification. Because this class is called with
 * Intent, you need to put extra content in the intent, like the type of the
 * notification, id of the notification etc.
 * 
 * @author Vladimir
 * 
 */
public class NotificationService extends IntentService {

	private String mMessage;
	private NotificationCompat.Builder builder;

	public NotificationService() {
		super("ch.cern.alice.alimonalisa");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String action = intent.getAction();

		if (action.equals(CommonConstants.ONE_NOTIFICATION_ACTION)) {
			issueOneNotification(intent);
		} else if (action.equals(CommonConstants.MULTIPLE_NOTIFICATIONS_ACTION)) {
			issueMultipleNotifications(intent);
		}

		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
	}

	private void issueOneNotification(Intent intent) {
		int id = 1;

		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

		builder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.alice_logo_ldpi)
				.setContentTitle("Title of the notification")
				.setDefaults(Notification.DEFAULT_ALL);

		Intent resultIntent = new Intent(this,
				NotificationDetailsActivity.class);
		// the id of the notification
		resultIntent.putExtra(CommonConstants.NOTIFICATION_ID, intent
				.getExtras().getInt(CommonConstants.NOTIFICATION_ID));

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the back stack
		stackBuilder.addParentStack(NotificationDetailsActivity.class);
		// Adds the Intent to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		// Gets a PendingIntent containing the entire back stack
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		builder.setContentIntent(resultPendingIntent);

		nm.notify(id, builder.build());
	}

	private void issueMultipleNotifications(Intent intent) {
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
	}

}
