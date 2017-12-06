package nhan1303.watsong.model;

import com.facebook.AccessToken;

import java.io.Serializable;

/**
 * Created by NHAN on 10/11/2017.
 */

public class InfoSong implements Serializable{
    String titleTrack;
    String artistName;
    String urlImage = "";
    int icPlaceHolder = 0;
    String currentTime;
    String idYoutube ="";
    AccessToken accessToken;

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public InfoSong() {
    }

    public InfoSong(String titleTrack, String artistName, String urlImage, String currentTime, String vid) {
        this.titleTrack = titleTrack;
        this.artistName = artistName;
        this.urlImage    = urlImage;
        this.idYoutube = vid;
        this.currentTime = currentTime;
    }

    public InfoSong(String titleTrack, String artistName, int icPlaceHolder, String currentTime, String vid) {
        this.titleTrack = titleTrack;
        this.artistName = artistName;
        this.idYoutube = vid;
        this.currentTime = currentTime;
        this.icPlaceHolder = icPlaceHolder;
    }

    public String getTitleTrack() {
        return titleTrack;
    }

    public void setTitleTrack(String titleTrack) {
        this.titleTrack = titleTrack;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getIcPlaceHolder() {
        return icPlaceHolder;
    }

    public void setIcPlaceHolder(int icPlaceHolder) {
        this.icPlaceHolder = icPlaceHolder;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getIdYoutube() {
        return idYoutube;
    }

    public void setIdYoutube(String idYoutube) {
        this.idYoutube = idYoutube;
    }

    @Override
    public String toString() {
        return "InfoSong{" +
                "titleTrack='" + titleTrack + '\'' +
                ", artistName='" + artistName + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", icPlaceHolder=" + icPlaceHolder +
                ", currentTime='" + currentTime + '\'' +
                ", idYoutube='" + idYoutube + '\'' +
                '}';
    }
}
