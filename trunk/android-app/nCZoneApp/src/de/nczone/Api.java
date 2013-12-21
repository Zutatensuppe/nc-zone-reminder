package de.nczone;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Api {
	
	
	private int gameCount = 0;
	
	public int getGameCount() {
		return this.gameCount;
	}
	
	
	public int loggedInCount( String nickname ) {
		String str = "***";
    	int count = 0;

        try
    	{
    		HttpClient hc = new DefaultHttpClient();
    		HttpPost post = new HttpPost("http://www.new-chapter.eu/zone/api2.php?request=logged_in_list&nick="+nickname);
    		HttpResponse rp = hc.execute(post);

    		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    		{
    			str = EntityUtils.toString(rp.getEntity());
    		}
    		
    		// check again without nickname
    		if ( rp.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR ) {
        		post = new HttpPost("http://www.new-chapter.eu/zone/api2.php?request=logged_in_list");
        		rp = hc.execute(post);

        		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
        		{
        			str = EntityUtils.toString(rp.getEntity());
        		}
    		}
    		
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	
    	try {
    		Log.d("Api , loggedInCount()", str);
			JSONObject jsonObject = new JSONObject(str);
			JSONArray data = jsonObject.getJSONArray("data");
			count = data.length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("Api, loggedInCount()", e.toString());
		}
		return count;
	}
	
	public boolean isInGame( String nickname ) {
		String str = "***";

        try
    	{
    		HttpClient hc = new DefaultHttpClient();
    		HttpPost post = new HttpPost("http://www.new-chapter.eu/zone/api.php?request=running_matches");

    		HttpResponse rp = hc.execute(post);

    		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    		{
    			str = EntityUtils.toString(rp.getEntity());
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}  
    	
    	try {
    		Log.d("Api, isInGame()", str);
			JSONObject jsonObject = new JSONObject(str);
			JSONArray data = jsonObject.getJSONArray("data");
			JSONObject game, teams, team;
			JSONArray users;
			JSONObject user;
			
			// go through games
			for ( int i=0; i<data.length(); i++ ) {
				this.gameCount = data.length();
				
				// go through teams
				game = data.getJSONObject(i);
				teams = game.getJSONObject("teams");
				team = teams.getJSONObject("team1");
				users = team.getJSONArray("users");

				for ( int j=0; j<users.length(); j++ ) {
					user = users.getJSONObject(j);
					if ( user.getString("nick").equals(nickname) ) {
						return true;
					}
				}

				team = teams.getJSONObject("team2");
				users = team.getJSONArray("users");

				for ( int j=0; j<users.length(); j++ ) {
					user = users.getJSONObject(j);
					if ( user.getString("nick").equals(nickname) ) {
						return true;
					}
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("Api, isInGame()", e.toString());
		}
		return false;
	}
	
	

}
