package ch.cern.alice.alimonalisa.services;

import ch.cern.alice.alimonalisa.tasks.DownloadXmlTask;
import ch.cern.alice.alimonalisa.tasks.Downloader;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DownloadXmlTask d = new DownloadXmlTask(this);
		d.execute("http://alimonitor.cern.ch/atom.jsp");
		return Service.START_NOT_STICKY;
	}
	
	

}
