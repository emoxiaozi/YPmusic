package csu.wsq.musicplayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.adapter.MusicAdapter;
import csu.wsq.musicplayer.dao.FavoriteDao;
import csu.wsq.musicplayer.dao.MusicDao;
import csu.wsq.musicplayer.entity.Music;
import csu.wsq.musicplayer.service.PlayerService;
import csu.wsq.musicplayer.util.AppDatabase;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private final List<Music> favoriteMusicList = new ArrayList<>();
    private FavoriteDao favoriteDao;
    private boolean isBound = false;
    private ServiceConnection serviceConnection;
    private PlayerService playerService;
    private final Executor executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.favorite_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initDataBase();
        initService();
    }

    private void initDataBase() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music-db").build();
        favoriteDao = db.favoriteDao();
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
                playerService = binder.getService();
                isBound = true;
                getFavoriteMusicList();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isBound) {
            getFavoriteMusicList();
        }
    }

    private void getFavoriteMusicList() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                favoriteMusicList.clear();
                List<Long> favoriteMusicIdList = favoriteDao.getAllFavoriteId();
                List<Music> musiclist = playerService.getMusiclist();
                for(Music music : musiclist) {
                    if(favoriteMusicIdList.contains(music.getId())) {
                        music.setLiked(true);
                        favoriteMusicList.add(music);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout noContentTextView = findViewById(R.id.no_content_ll);
                        if(favoriteMusicList.isEmpty()) {
                            noContentTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else{
                            noContentTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            MusicAdapter musicAdapter = new MusicAdapter(favoriteMusicList);
                            musicAdapter.setOnItemClickListener(FavoriteActivity.this::onItemClick);
                            recyclerView.setAdapter(musicAdapter);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
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