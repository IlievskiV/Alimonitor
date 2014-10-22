package ch.cern.alice.alimonalisa;

import java.text.ParseException;

import ch.cern.alice.alimonalisa.contentprovider.ContentProviderUtils;
import ch.cern.alice.alimonalisa.contentprovider.NotificationContentProvider;
import ch.cern.alice.alimonalisa.model.Notification;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationDetailsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_details);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FragmentManager fm = getSupportFragmentManager();
		TextView textView = (TextView) fm.findFragmentById(R.id.top_fragment)
				.getView().findViewById(R.id.title);

		WebView webView = (WebView) findViewById(R.id.notificationContent);

		int id = getIntent().getExtras()
				.getInt(CommonConstants.NOTIFICATION_ID);

		Uri rowAddress = ContentUris.withAppendedId(
				NotificationContentProvider.CONTENT_URI, id);

		ContentResolver cr = getContentResolver();

		try {
			Cursor c = cr.query(rowAddress, null, null, null, null);
			if(c.moveToFirst()){
				Notification n = ContentProviderUtils.cursorToNotification(c);
				webView.loadData(n.getDescription(), "text/html", "UTF-8");
			}else{
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		textView.setText(""
				+ getIntent().getExtras().getInt(
						CommonConstants.NOTIFICATION_ID));
	}
}
