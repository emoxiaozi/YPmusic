package csu.wsq.musicplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "music_in_folder")
public class MusicInFolder implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "music_id")
    private long musicId;
    @ColumnInfo(name = "folder_name")
    private String foldName;

    public MusicInFolder(long musicId, String foldName) {
        this.musicId = musicId;
        this.foldName = foldName;
    }

    protected MusicInFolder(Parcel in) {
        id = in.readLong();
        musicId = in.readLong();
        foldName = in.readString();
    }

    public static final Creator<MusicInFolder> CREATOR = new Creator<MusicInFolder>() {
        @Override
        public MusicInFolder createFromParcel(Parcel in) {
            return new MusicInFolder(in);
        }

        @Override
        public MusicInFolder[] newArray(int size) {
            return new MusicInFolder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(musicId);
        dest.writeString(foldName);
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }

    public String getFoldName() {
        return foldName;
    }

    public void setFoldName(String foldName) {
        this.foldName = foldName;
    }
}