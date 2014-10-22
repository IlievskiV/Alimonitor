package ch.cern.alice.alimonalisa;

import net.danlew.android.joda.JodaTimeAndroid;
import android.app.Application;

public class NotificationsApp extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		JodaTimeAndroid.init(this);
	}

	
	
}
