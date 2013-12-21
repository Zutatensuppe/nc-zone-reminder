package de.nczone;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class NCZoneAppObserverService extends Service {
	
	public static final String NICKNAME = "nickname";
	public static final String COUNT_LOGGED_IN = "count_logged_in";
	public static final String COUNT_RUNNING_MATCHES = "count_running_matches";
	public static final String IS_IN_GAME = "is_in_game";
	
	public static final String NOTIFICATION = "de.nczone.NCZoneAppObserverService";

	
	private NCZoneObserverTask observerTask = null;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		

	    String nickname = intent.getStringExtra(NICKNAME);
	    observerTask = new NCZoneObserverTask();
		observerTask.execute(nickname);
		return Service.START_NOT_STICKY;
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d("ObserverService", "onDestroy()...");
		if ( observerTask != null ) {
			observerTask.cancel(true);
			observerTask = null;
		}
		super.onDestroy();
	}
	
	public void doBroadcast(Integer... values ) {

		Intent intent = new Intent(NOTIFICATION);
	    intent.putExtra(COUNT_LOGGED_IN, values[1]);
	    intent.putExtra(COUNT_RUNNING_MATCHES, values[0]);
	    intent.putExtra(IS_IN_GAME, values[2] == 1);
		this.sendBroadcast(intent);
		
	};
	
	
	private class NCZoneObserverTask extends AsyncTask<String, Integer, Boolean> {


		@Override
		protected Boolean doInBackground(String... params) {

	        Api api = new Api();
			while ( true ) {
				Log.d("NCZoneObserverTask??", isCancelled() + "" );
				if ( isCancelled() ) break;
				
				
				
				int isInGame = 0;
				if ( !api.isInGame(params[0]) ) {
					Log.d("NCZoneObserverTask", params[0] + " is not in game");
				} else {
					// break out of loop
					Log.d("NCZoneObserverTask", params[0] + " is in game");
					isInGame = 1;
				}
				publishProgress(api.getGameCount(), api.loggedInCount(params[0]), isInGame);
				
				

                try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			doBroadcast(values);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		}

    }
}
