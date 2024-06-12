package csu.wsq.musicplayer.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.room.Insert;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import csu.wsq.musicplayer.dao.FavoriteDao;
import csu.wsq.musicplayer.dao.MusicDao;
import csu.wsq.musicplayer.entity.Music;
import csu.wsq.musicplayer.util.AppDatabase;

public class PlayerService extends Service {

    public static final int MODE_ORDER = 0;
    public static final int MODE_LOOP = 1;
    public static final int MODE_RANDOM = 2;
    private final IBinder binder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private final List<Music> playlist = new ArrayList<>();
    private final List<Music> musicList = new ArrayList<>();
    private int currentSongIndex = 0;
    private int mode = MODE_ORDER;
    private static final int RECENT_PLAY_LIMIT = 5;
    private MusicDao musicDao;
    private FavoriteDao favoriteDao;
    private final Executor executor = Executors.newSingleThreadExecutor();


    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mediaPlayer = new MediaPlayer();
        initDataBase();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Handle start command if needed
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void initDataBase() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music-db").build();
        musicDao = db.musicDao();
        favoriteDao = db.favoriteDao();
    }

    public void switchMode(){
        mode = (mode + 1) % 3;
    }

    private void sendMusicChangeBroadcast() {
        Intent intent = new Intent("PLAY_MUSIC_CHANGED");
        sendBroadcast(intent);
    }

    public boolean isPlaying() {
       return mediaPlayer.isPlaying();
    }

    public void initMusicList(List<Music> musicList) {
        this.musicList.addAll(musicList);
    }

    public void setPlaylist(List<Music> newPlaylist) {
        playlist.clear();
        playlist.addAll(newPlaylist);
        checkIfLike();
        currentSongIndex = 0;
        setSong(currentSongIndex);
        // Prepare the first song
        prepareSong(playlist.get(currentSongIndex).getData());
    }

    public List<Music> getMusiclist() {
        return musicList;
    }

    public Music getCurrentMusic() {
        return playlist.get(currentSongIndex);
    }

    public void setSong(int index) {
        if (index >= 0 && index < playlist.size()) {
            sendMusicChangeBroadcast();
            currentSongIndex = index;
            Music currentMusic = playlist.get(currentSongIndex);
            addToRecentList(currentMusic);
            prepareSong(currentMusic.getData());
        }
    }

    public void playMusic(Music music) {
        for(int i = 0; i < playlist.size(); i++){
            if(playlist.get(i).getId() == music.getId()){
                setSong(i);
                play();
                return;
            }
        }
        playlist.add(currentSongIndex + 1, music);
        setSong(currentSongIndex - 1);
        play();
    }

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void next() {
        int next_id = currentSongIndex;
        switch (mode) {
            case MODE_ORDER:
                next_id = (currentSongIndex + 1) % playlist.size();
                break;
            case MODE_LOOP:
                // 从头播放当前歌曲
                mediaPlayer.seekTo(0);
                break;
            case MODE_RANDOM:
                int add = (int) (Math.random() * playlist.size());
                next_id = (currentSongIndex + add) % playlist.size();
                break;
        }
        setSong(next_id);
        play();
    }

    public void previous() {
        int pre_id = currentSongIndex;
        switch (mode) {
            case MODE_ORDER:
                pre_id = (currentSongIndex - 1 + playlist.size()) % playlist.size();
                break;
            case MODE_LOOP:
                // 从头播放当前歌曲
                mediaPlayer.seekTo(0);
                break;
            case MODE_RANDOM:
                pre_id = (int) (Math.random() * playlist.size());
                break;
        }
        setSong(pre_id);
        play();
    }

    private void prepareSong(String songPath) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 检查是否收藏，并且将结果添加到播放列表的音乐中
    private void checkIfLike() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Long> musicIdList = favoriteDao.getAllFavoriteId();
                for (Music music : playlist) {
                    if(musicIdList.contains(music.getId())){
                        music.setLiked(true);
                    }
                }
            }
        });
    }

    private void addToRecentList(final Music music) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                music.setFolder("Recent");
                List<Long> allId = musicDao.getRecentIdList();
                if(allId.contains(music.getId())){
                    musicDao.delete(music);
                }
                musicDao.insert(music);
                List<Music> recentMusic = musicDao.getRecentMusicList();
                if (recentMusic.size() > RECENT_PLAY_LIMIT) {
                    musicDao.delete(recentMusic.get(recentMusic.size() - 1));
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public String getConvertedTime(){
        int curTime = mediaPlayer.getCurrentPosition();
        int minutes = curTime / 60000;
        int seconds = curTime / 1000 % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}