package csu.wsq.musicplayer.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import csu.wsq.musicplayer.entity.Music;

@Dao
public interface MusicDao {
    @Insert
    void insert(Music music);

    @Delete
    void delete(Music music);

    @Update
    void update(Music music);

    @Query("SELECT * FROM music")
    List<Music> getAllMusic();

    @Query("SELECT * FROM music WHERE folder = 'Recent' ORDER BY rowid DESC")
    List<Music> getRecentMusicList();

    @Query("SELECT id FROM music WHERE folder = 'Recent'")
    List<Long> getRecentIdList();
}
