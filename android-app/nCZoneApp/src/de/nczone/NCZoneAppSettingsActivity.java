package de.nczone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class NCZoneAppSettingsActivity extends Activity {


	
	private Button btnCancel;
	private Button btnSave;
	private Button btnTestAlarm;

	private CheckBox chkVibrate;
	private CheckBox chkPlaysounds;
	
	private Spinner spnSoundSelector;
	
	
	private Vibrator vib;
	private MediaPlayer defaultMediaPlayer, mediaPlayer7, mediaPlayer14;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

    	this.vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		SharedPreferences settings = getSharedPreferences(NCZoneAppActivity.PREFS_NAME, 0);
		boolean vibrate = settings.getBoolean("vibrate", true);
		boolean playsounds = settings.getBoolean("playsounds", true);
        String nickname = settings.getString("nickname", "");
        String soundfile = settings.getString("soundfile", "default");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.sounds_array, android.R.layout.simple_spinner_item);
        int i = adapter.getPosition(soundfile);
		
		
		chkVibrate = (CheckBox)findViewById(R.id.vibrate);
		chkVibrate.setChecked(vibrate);
		
		chkPlaysounds = ((CheckBox)findViewById(R.id.playSounds));
		chkPlaysounds.setChecked(playsounds);
		
        ((TextView)findViewById(R.id.editText1)).setText(nickname);
        
        
        
        spnSoundSelector = (Spinner) findViewById(R.id.soundSelector);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSoundSelector.setAdapter(adapter);
        
        spnSoundSelector.setSelection(i);
        
        this.btnTestAlarm = (Button)findViewById(R.id.btnTestAlarm);
		this.btnTestAlarm.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try {
		        if ( chkVibrate.isChecked() ) {
		        	vib.vibrate(1000);
		        }
		        if ( chkPlaysounds.isChecked() ) {
		        	String item = spnSoundSelector.getSelectedItem().toString();
		        	
		        	if ( item.equals("default")) {
		        		if ( defaultMediaPlayer == null )
		            	defaultMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.notify);
		        		defaultMediaPlayer.start();
		        	} else if ( item.equals("7 Ah!") ) {
		        		if ( mediaPlayer7 == null )
		            	mediaPlayer7 = MediaPlayer.create(getBaseContext(), R.raw.taunt7);
		        		mediaPlayer7.start();
		        	} else if ( item.equals("14 Fang endlich an!") ) {
		        		if ( mediaPlayer14 == null )
		        			mediaPlayer14 = MediaPlayer.create(getBaseContext(), R.raw.taunt14);
		        		mediaPlayer14.start();
		        	}
		        }
				} catch( NullPointerException e ) {
					Log.d("Settings", e.toString());
				}
			}
		});
        
        
		this.btnCancel = (Button)findViewById(R.id.btnCancel);
		this.btnCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		this.btnSave = (Button)findViewById(R.id.btnSave);
		this.btnSave.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(NCZoneAppActivity.PREFS_NAME, 0);
				Log.d("settings before", settings.getString("nickname", ""));

				SharedPreferences.Editor editor = settings.edit();
				editor.putString("nickname", ((TextView)findViewById(R.id.editText1)).getText().toString());
				editor.putBoolean("vibrate", chkVibrate.isChecked());
				editor.putBoolean("playsounds", chkPlaysounds.isChecked());
	        	editor.putString("soundfile", spnSoundSelector.getSelectedItem().toString());
				editor.commit();
				Log.d("settings after", settings.getString("nickname", ""));
				
				
				
				setResult(RESULT_OK);
				
				finish();
			}
		});
	}
}
