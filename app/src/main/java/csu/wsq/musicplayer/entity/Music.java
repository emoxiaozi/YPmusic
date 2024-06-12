package csu.wsq.musicplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "music")
public class Music implements Parcelable {

    @PrimaryKey()
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "album")
    private String album;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "folder")
    private String folder;

    private boolean liked = false;

    public Music(long id, String title, String album, String artist, String data, String duration) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.data = data;
        this.duration = duration;
    }

    protected Music(Parcel in) {
        id = in.readLong();
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        data = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(data);
        dest.writeString(duration);
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getData() {
        return data;
    }

    public String getDuration() {
        return duration;
    }

    public String getFolder() {
        return folder;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}