package nhan1303.watsong;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import nhan1303.watsong.model.InfoSong;

/**
 * Created by HONGKHANH on 18/11/2017.
 */


public class ReadJson extends AsyncTask<String, Void, String> {


    String videoId;
    String song = "khong the noi";
    Context context;
    String title_track;
    String artist_name;


    InfoSong infoSong = new InfoSong();

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public ReadJson(String title_track, String artist_name) {
        this.title_track = title_track;
        this.artist_name = artist_name;
    }

    public ReadJson() {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String jsonYouTube = ReadUrl("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q="+title_track+"-"+artist_name+"&key=AIzaSyDHd4sAjoUsi7Gme2g6dHHClZpTQsLyT4E");
        ReadIdYouTube(jsonYouTube);
        return jsonYouTube;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private void ReadIdYouTube(String s) {
        try {
            JSONObject root = new JSONObject(s);

            JSONArray item = root.getJSONArray("items");
            JSONObject items = (JSONObject) item.get(0);
            JSONObject id = items.getJSONObject("id");
            videoId = id.getString("videoId");
            setVideoId(videoId);
            Log.d("videoIdReadJson", videoId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String ReadUrl(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();

    }

}
