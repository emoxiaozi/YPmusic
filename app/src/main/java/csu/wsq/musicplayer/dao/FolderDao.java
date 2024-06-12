package csu.wsq.musicplayer.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import csu.wsq.musicplayer.entity.Folder;

@Dao
public interface FolderDao {
    @Insert
    void insert(Folder folder);

    @Delete
    void delete(Folder folder);

    @Query("SELECT * FROM folder")
    List<Folder> getAllFolders();
}
