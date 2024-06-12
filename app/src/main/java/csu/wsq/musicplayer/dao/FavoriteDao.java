package csu.wsq.musicplayer.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import csu.wsq.musicplayer.entity.Album;
import csu.wsq.musicplayer.entity.Favorite;
import csu.wsq.musicplayer.entity.Music;

@Dao
public interface FavoriteDao {
    @Insert
    void insert(Favorite favorite);

    @Query("DELETE FROM favorite WHERE music_id = :musicId")
    void deleteByMusicId(Long musicId);


    @Query("SELECT music_id FROM favorite")
    List<Long> getAllFavoriteId();
}
