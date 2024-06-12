package csu.wsq.musicplayer.util;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import csu.wsq.musicplayer.dao.AlbumDao;
import csu.wsq.musicplayer.dao.FavoriteDao;
import csu.wsq.musicplayer.dao.FolderDao;
import csu.wsq.musicplayer.dao.MusicDao;
import csu.wsq.musicplayer.entity.Album;
import csu.wsq.musicplayer.entity.Favorite;
import csu.wsq.musicplayer.entity.Folder;
import csu.wsq.musicplayer.entity.Music;

@Database(entities = {Music.class, Album.class, Folder.class, Favorite.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MusicDao musicDao();

    public abstract FolderDao folderDao();

    public abstract AlbumDao albumDao();

    public abstract FavoriteDao favoriteDao();


//    TODO:完善最近播放列表，加入插入时间
//    private static AppDatabase INSTANCE;

//    public static synchronized AppDatabase getInstance(Context context) {
//        if (INSTANCE == null) {
//            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            AppDatabase.class, "music-db")
//                    .addCallback(new RoomDatabase.Callback() {
//                        @Override
//                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                            super.onCreate(db);
//                            db.execSQL("CREATE TRIGGER set_update_time_trigger " +
//                                    "AFTER INSERT ON music_table " +
//                                    "BEGIN " +
//                                    "UPDATE music_table SET update_time = strftime('%s','now') WHERE rowid = new.rowid; " +
//                                    "END;");
//                            db.execSQL("CREATE TRIGGER update_update_time_trigger " +
//                                    "AFTER UPDATE ON music_table " +
//                                    "BEGIN " +
//                                    "UPDATE music_table SET update_time = strftime('%s','now') WHERE rowid = new.rowid; " +
//                                    "END;");
//                        }
//                    })
//                    .build();
//        }
//        return INSTANCE;
//    }
}
