package de.nczone;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NCZoneAppActivity extends Activity {
	

	// External Pref File Name
	public static final String PREFS_NAME = "nCZoneAppSettings";
	
	// REQUEST CODES
	private final int REQUEST_CODE_SETTINGS = 7331;
	
	// Settings
    private SharedPreferences settings;
    
	// Tasks
	// private NCZoneObserverTask observerTask;
	
	
	// GraphicalElements
	private TextView txtNickname;
	private TextView txtInGame;
	private TextView txtRunningMatches;
	private TextView txtLoggedInCount;
	private ToggleButton toggleOnOff;
	private Button buttonSettings;
	


	private Vibrator vib;
	private MediaPlayer defaultMediaPlayer, mediaPlayer7, mediaPlayer14;
	
	
	
	
	private BroadcastReceiver broadcastReciever = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
		
			Bundle bundle = intent.getExtras();
			if ( bundle != null ) {
		        int countLoggedIn = bundle.getInt(NCZoneAppObserverService.COUNT_LOGGED_IN, 0);
		        int countRunningMatches = bundle.getInt(NCZoneAppObserverService.COUNT_RUNNING_MATCHES, 0);
		        boolean isInGame = bundle.getBoolean(NCZoneAppObserverService.IS_IN_GAME, false);

				// logged in players count
		        txtLoggedInCount.setText("Es sind " + countLoggedIn + " Spieler eingeloggt.");
		        
		        // running matches
		        txtRunningMatches.setText("Es laufen " + countRunningMatches + " Spiele.");
		        
		        // is user in game?
		        if ( isInGame ) {
		        	
		        	
			    	notifyInGame(); // notify the user
		        	turnOff(); // turn off
		        	
		        	
		        } else {
		        	txtInGame.setText("Kein Spiel mit dir :(");
		        }
		        
			}
			
		}
	};
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        

    	this.vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        this.loadSettings();
        
        this.initInterface();
        
        this.initTasks();
        
    }

    private void loadSettings() {
    	this.settings = getSharedPreferences(PREFS_NAME, 0);
    }
    
    public boolean isOn() {
    	return (this.settings.getBoolean("onOff", false));
    }
    public String getNickname() {
    	return (this.settings.getString("nickname", ""));
    }
    public boolean isVibrate() {
    	return (settings.getBoolean("vibrate", false));
    }
    public boolean isPlaysounds() {
    	return (settings.getBoolean("playsounds", false));
    }
    public String getSoundfile() {
    	return (settings.getString("soundfile", "default"));
    }
    
    
    public boolean notifyInGame() {

    	// prepare intent which is triggered if the
    	// notification is selected

    	this.txtInGame.setText("Du bist in einem Spiel!");

        if ( this.isVibrate() ) {
        	this.vib.vibrate(1000);
        }
        
        if ( this.isPlaysounds() ) {
        	
        	
        	if ( this.getSoundfile().equals("default")) {
        		this.defaultMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.notify);
        		this.defaultMediaPlayer.start();
        	} else if ( this.getSoundfile().equals("7 Ah!") ) {
        		this.mediaPlayer7 = MediaPlayer.create(getBaseContext(), R.raw.taunt7);
        		this.mediaPlayer7.start();
        	} else if ( this.getSoundfile().equals("14 Fang endlich an!") ) {
        		this.mediaPlayer14 = MediaPlayer.create(getBaseContext(), R.raw.taunt14);
            	this.mediaPlayer14.start();
        	}
        }

        
        
        /* Create a Notification 
        ================================================== */
        
    	Intent intent = new Intent(this, NCZoneAppActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	// intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    	PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

    	// build notification
    	// the addAction re-use the same intent to keep the example short
    	NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    	Notification notification = builder
	        .setContentTitle("nC Zone Reminder - Im Spiel")
	        .setContentText(this.getNickname() + ", du bist in einem Spiel!")
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .setAutoCancel(true)
	        .addAction(R.drawable.ic_launcher, "Call", pIntent)
	        .addAction(R.drawable.ic_launcher, "More", pIntent)
	        .addAction(R.drawable.ic_launcher, "And more", pIntent).build();
    	NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	notificationManager.notify(0, notification);
    	
    	
    	return true;
    }
    
    
    private void initInterface() {

        this.txtNickname = (TextView)findViewById(R.id.nickname);
        this.txtNickname.setText(this.getNickname());

        
        this.txtInGame = (TextView)findViewById(R.id.inGame);
        this.txtRunningMatches = (TextView)findViewById(R.id.runningMatches);
        this.txtLoggedInCount = (TextView)findViewById(R.id.loggedInCount);
        
        this.buttonSettings = (Button)findViewById(R.id.buttonSettings);
        this.buttonSettings.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		// Toast.makeText(getBaseContext(), "settings clicked!", Toast.LENGTH_SHORT).show();
                startActivityForResult(
                	new Intent(v.getContext(), NCZoneAppSettingsActivity.class),
                	REQUEST_CODE_SETTINGS
                );
        	}
        });

        this.toggleOnOff = (ToggleButton)findViewById(R.id.buttonOnOff);
        this.toggleOnOff.setChecked(this.isOn());
        this.toggleOnOff.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if ( ((ToggleButton)v).isChecked() ) {
    				toggleOnOff.setChecked(turnOn());
				} else {
					turnOff();
				}
				// Toast.makeText(getBaseContext(), (((ToggleButton)v).isChecked()? "Angeschaltet!" : "Ausgeschaltet!"), Toast.LENGTH_SHORT).show();
			}
		});
    }
    
    
    
    private void initTasks() {
    	
    	if ( this.getNickname().length()>0 ) {
    		this.toggleOnOff.setEnabled(true);
    		this.toggleOnOff.setChecked(false);
    	} else if ( this.isOn() ) {
    		this.toggleOnOff.setChecked(turnOn());
        }
    	
    	
        registerReceiver(this.broadcastReciever, new IntentFilter(NCZoneAppObserverService.NOTIFICATION));
    	
    }
    
    
    
    
    
    
    public void setNickname( String nickname ) {

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	// NCZoneAppSettingsActivity
    	if ( requestCode == REQUEST_CODE_SETTINGS ) {
    		
    		// Ok Button was clicked
    		if ( resultCode == RESULT_OK ) {
    			
    			txtNickname.setText(this.getNickname());

    			// Task is on? Then reset the Task
    			if ( this.isOn() ) {
    				turnOff();
    				toggleOnOff.setChecked(turnOn());
    			}
    			
				Toast.makeText(getBaseContext(), "Gespeichert!", Toast.LENGTH_SHORT).show();
			
    		}
    		// Cancel Button was clicked
    		else if ( resultCode == RESULT_CANCELED ) {
    			
				// Toast.makeText(getBaseContext(), "Einstellungen wurden nicht gespeichert!", Toast.LENGTH_SHORT).show();
				
    		}
    		
    		
    	} // END Settings Activity
    	
    	//
    }
    
    
    
    
    private boolean turnOn () {
    	if ( this.getNickname().length()>0 ) {
    		
    		Intent i= new Intent(this, NCZoneAppObserverService.class);
    		i.putExtra(NCZoneAppObserverService.NICKNAME, this.getNickname());
    		this.startService(i);
    		
//	    	observerTask = new NCZoneObserverTask();
//	        observerTask.execute(this.getNickname());
	        return true;
    	}
    	return false;
    }
    
    private void turnOff () {
		
		Intent i= new Intent(this, NCZoneAppObserverService.class);
		this.stopService(i);
		
//    	observerTask.cancel(true);
//    	observerTask = null;
    }
    
}