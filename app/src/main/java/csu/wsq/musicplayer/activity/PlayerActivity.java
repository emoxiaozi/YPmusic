package csu.wsq.musicplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.dao.FavoriteDao;
import csu.wsq.musicplayer.entity.Favorite;
import csu.wsq.musicplayer.entity.Music;
import csu.wsq.musicplayer.service.PlayerService;
import csu.wsq.musicplayer.util.AppDatabase;

public class PlayerActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView currentTime;
    private final Handler handler = new Handler();
    private Button btnPlayPause;

    private Button btnBack;
    private PlayerService playerService;
    private boolean isBound = false;
    private Music currentMusic;
    private int mode = PlayerService.MODE_ORDER;
    private boolean isPlaying = false;
    private BroadcastReceiver broadcastReceiver;
    public FavoriteDao favoriteDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            playerService = binder.getService();
            isBound = true;
            refreshInfo();
            initSeekBar();
            initButtons();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        initReceiver();
        initDataBase();

        seekBar = findViewById(R.id.seekBar);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnBack =findViewById(R.id.btnBack);
    }

    // 初始化广播接收器
    private void initReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), "PLAY_MUSIC_CHANGED")) {
                    refreshInfo();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAY_MUSIC_CHANGED");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // For API level 26 and above
            registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            // For API level 25 and below
            registerReceiver(broadcastReceiver, filter);
        }
    }

    private void initDataBase() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music-db").build();
        favoriteDao = db.favoriteDao();
    }

    private void refreshInfo() {
        isPlaying = playerService.getMediaPlayer().isPlaying();
        currentMusic = playerService.getCurrentMusic();
        // 设置歌曲信息
        TextView title = findViewById(R.id.musicTitle);
        title.setText(currentMusic.getTitle());
        TextView artist = findViewById(R.id.artistName);
        artist.setText(currentMusic.getArtist());
        TextView totalTime = findViewById(R.id.totalTime);
        totalTime.setText(currentMusic.getDuration());
        Button favorite = findViewById(R.id.favButton);
        favorite.setBackgroundResource(currentMusic.isLiked() ? R.drawable.ic_liked : R.drawable.ic_like);
        currentTime = findViewById(R.id.currentTime);
    }

    private void initSeekBar() {
        seekBar.setMax(playerService.getMediaPlayer().getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerService.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        handler.postDelayed(updateSeekBar, 1000);
    }

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(playerService.getMediaPlayer().getCurrentPosition());
            currentTime.setText(playerService.getConvertedTime());
            handler.postDelayed(this, 1000);
        }
    };

    private void initButtons() {
        findViewById(R.id.btnPrevious).setOnClickListener(v -> playPrevious());
        findViewById(R.id.btnNext).setOnClickListener(v -> playNext());
        btnPlayPause.setBackgroundResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);

        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                playerService.pause();
                btnPlayPause.setBackgroundResource(R.drawable.ic_play);
            } else {
                playerService.play();
                btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
            }
            isPlaying = !isPlaying;
        });

        findViewById(R.id.downLoadButton).setOnClickListener(v -> downLoadMusic());
        findViewById(R.id.favButton).setOnClickListener(v -> addToFavorites());
        findViewById(R.id.modeButton).setOnClickListener(v -> setPlayMode());
        findViewById(R.id.addButton).setOnClickListener(v -> addToMusicList());
        findViewById(R.id.menuButton).setOnClickListener(v -> showMenu());
        findViewById(R.id.btnBack).setOnClickListener(v -> backHome());
    }

    private void downLoadMusic() {
        // 下载歌曲逻辑
        Toast.makeText(this, "歌曲已保存在本地", Toast.LENGTH_SHORT).show();
    }

    private void backHome(){
        Intent homeIntent = new Intent(PlayerActivity.this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void playPrevious() {
        // 实现播放上一首的逻辑
        playerService.previous();
        refreshInfo();
    }

    private void playNext() {
        // 实现播放下一首的逻辑
        playerService.next();
        refreshInfo();
    }

    private void addToFavorites() {
        // 实现收藏逻辑
        if(currentMusic.isLiked()) {
            currentMusic.setLiked(false);
            Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
            executor.execute(() -> favoriteDao.deleteByMusicId(currentMusic.getId()));
        } else {
            currentMusic.setLiked(true);
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
            executor.execute(() -> favoriteDao.insert(new Favorite(currentMusic.getId())));
        }
        refreshInfo();
    }

    private void addToMusicList() {
        // 实现添加到歌单逻辑
    }

    private void showMenu() {
        // 实现弹出菜单逻辑
    }

    private void setPlayMode() {
        // 实现设置播放模式的逻辑
        playerService.switchMode();
        mode = (mode + 1) % 3;
        Button button = findViewById(R.id.modeButton);
        if (mode == PlayerService.MODE_LOOP) {
            Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
            button.setBackgroundResource(R.drawable.ic_single_repeat);
        } else if (mode == PlayerService.MODE_RANDOM) {
            Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
            button.setBackgroundResource(R.drawable.ic_random);
        } else {
            Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
            button.setBackgroundResource(R.drawable.ic_order);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
