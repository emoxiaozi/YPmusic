package csu.wsq.musicplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "album")
public class Album implements Parcelable {
    @PrimaryKey()
    private long id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "artist")
    private String artist;
    @ColumnInfo(name = "cover_image_url")
    private String coverImageUrl;
    @ColumnInfo(name = "song_count")
    private int songCount;

    public Album(long id, String title, String artist, String coverImageUrl, int songCount) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.coverImageUrl = coverImageUrl;
        this.songCount = songCount;
    }

    protected Album(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
        coverImageUrl = in.readString();
        songCount = in.readInt();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
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
        dest.writeString(artist);
        dest.writeString(coverImageUrl);
        dest.writeInt(songCount);
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}