package csu.wsq.musicplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "folder")
public class Folder implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "cover_image_url")
    private String coverImageUrl;
    private int musicCount;

//    @ColumnInfo(name = "song_count")
//    private int songCount;


    @Ignore
    public Folder(String name) {
        this.name = name;
    }

    public Folder(long id, String name, String coverImageUrl) {
        this.id = id;
        this.name = name;
        this.coverImageUrl = coverImageUrl;
    }

    protected Folder(Parcel in) {
        id = in.readLong();
        name = in.readString();
        coverImageUrl = in.readString();
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(coverImageUrl);
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getMusicCount() {
        return musicCount;
    }

    public void setMusicCount(int musicCount) {
        this.musicCount = musicCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}