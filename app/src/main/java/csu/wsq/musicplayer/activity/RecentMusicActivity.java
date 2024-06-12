package csu.wsq.musicplayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.adapter.MusicAdapter;
import csu.wsq.musicplayer.dao.MusicDao;
import csu.wsq.musicplayer.entity.Music;
import csu.wsq.musicplayer.service.PlayerService;
import csu.wsq.musicplayer.util.AppDatabase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecentMusicActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Music> recentMusicList;
    private MusicDao musicDao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private boolean isBound = false;
    private ServiceConnection serviceConnection;
    private PlayerService playerService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_music);

        recyclerView = findViewById(R.id.recent_music_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initDataBase();
        initService();
        getRecentMusicList();
    }

    private void initDataBase() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music-db").build();
        musicDao = db.musicDao();
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
                playerService = binder.getService();
                isBound = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private void getRecentMusicList() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                recentMusicList = musicDao.getRecentMusicList();
                MusicAdapter musicAdapter = new MusicAdapter(recentMusicList);
                musicAdapter.setOnItemClickListener(RecentMusicActivity.this::onItemClick);
                recyclerView.setAdapter(musicAdapter);
            }
        });
    }

    public void onItemClick(Music music) {
        // 处理点击事件
        if (isBound) {
            playerService.playMusic(music); // 调用Service的方法
            Intent intent = new Intent(this, PlayerActivity.class);
            startActivity(intent);
        }
    }
}