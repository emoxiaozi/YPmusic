package csu.wsq.musicplayer.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import csu.wsq.musicplayer.entity.Album;
import csu.wsq.musicplayer.entity.Music;

@Dao
public interface AlbumDao {
    @Insert
    void insert(Music music);

    @Delete
    void delete(Music music);

    @Update
    void update(Music music);

    @Query("SELECT * FROM album")
    List<Album> getAllAlbum();
}
