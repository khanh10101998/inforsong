package nhan1303.watsong.model;

import java.io.Serializable;

/**
 * Created by NHAN on 10/11/2017.
 */

public class InfoSong implements Serializable{
    String title_track;
    String artist_name;
    String urlImage = "";
    int ic_place_holder = 0;
    String currentTime;
    String id_youtube="";



    public InfoSong() {
    }

    public InfoSong(String title_track, String artist_name, String urlImage, String currentTime) {
        this.title_track = title_track;
        this.artist_name = artist_name;
        this.urlImage = urlImage;
        this.currentTime = currentTime;
    }

    public InfoSong(String title_track, String artist_name, int ic_place_holder, String currentTime) {
        this.title_track = title_track;
        this.artist_name = artist_name;
        this.currentTime = currentTime;
        this.ic_place_holder = ic_place_holder;
    }

    public InfoSong(String title_track, String artist_name, String urlImage, String currentTime, String vid) {
        this.title_track = title_track;
        this.artist_name = artist_name;
        this.urlImage    = urlImage;
        this.id_youtube  = vid;
        this.currentTime = currentTime;
    }

    public InfoSong(String title_track, String artist_name, int ic_place_holder, String currentTime, String vid) {
        this.title_track = title_track;
        this.artist_name = artist_name;
        this.id_youtube  = vid;
        this.currentTime = currentTime;
        this.ic_place_holder = ic_place_holder;
    }


    public int getIc_place_holder() {
        return ic_place_holder;
    }

    public void setIc_place_holder(int ic_place_holder) {
        this.ic_place_holder = ic_place_holder;
    }

    public String getTitle_track() {
        return title_track;
    }

    public void setTitle_track(String title_track) {
        this.title_track = title_track;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getId_youtube() {
        return id_youtube;
    }

    public void setId_youtube(String id_youtube) {
        id_youtube = id_youtube;
    }
    @Override
    public String toString() {
        return "InfoSong{" +
                "title_track='" + title_track + '\'' +
                ", artist_name='" + artist_name + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", currentTime='" + currentTime + '\'' +
                ", ic_place_holder=" + ic_place_holder +
                '}';
    }
}
