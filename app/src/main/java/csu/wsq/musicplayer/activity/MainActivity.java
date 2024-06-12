package csu.wsq.musicplayer.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.blankj.utilcode.util.PermissionUtils;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.adapter.FolderAdapter;
import csu.wsq.musicplayer.adapter.MusicAdapter;
import csu.wsq.musicplayer.dao.FolderDao;
import csu.wsq.musicplayer.entity.Folder;
import csu.wsq.musicplayer.entity.Music;
import csu.wsq.musicplayer.service.PlayerService;
import csu.wsq.musicplayer.util.AppDatabase;
import csu.wsq.musicplayer.util.LocalLoader;

public class MainActivity extends AppCompatActivity {
    private boolean isPlaying = false;
    private List<Music> musicList;
    private final List<Folder> folderList = new ArrayList<>();
    private FolderDao folderDao;
    private volatile boolean isBound = false;
    private ServiceConnection serviceConnection;
    private PlayerService playerService;
    private PlayStateReceiver playStateReceiver;
    private LocalLoader localLoader = new LocalLoader(this);
    private static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ_MEDIA_AUDIO = Manifest.permission.READ_MEDIA_AUDIO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initReceiver();
        initClickListener();
        initDataBase();
        // 检查并请求权限
        checkPermissions();
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadMusic();
                } else {
                    Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                }
            });

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, PERMISSION_READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(PERMISSION_READ_MEDIA_AUDIO);
            } else {
                loadMusic();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                loadMusic();
            }
        }
    }


    private void loadMusic() {
        musicList = localLoader.loadLocalMusic();
        if (musicList == null || musicList.isEmpty()) {
            Toast.makeText(this, "没有找到音乐文件", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "成功加载音乐文件: " + musicList.size() + " 首", Toast.LENGTH_SHORT).show();
        }
        initService();
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshContent();
    }

    // 初始化广播接收器
    private void initReceiver() {
        playStateReceiver = new PlayStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("PLAY_MUSIC_CHANGED");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // For API level 26 and above
            registerReceiver(playStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            // For API level 25 and below
            registerReceiver(playStateReceiver, filter);
        }
    }

    private void initClickListener() {
        LinearLayout bottomPlayer = findViewById(R.id.bottom_player);
        Button playPauseButton = findViewById(R.id.button_play_pause);
        Button listButton = findViewById(R.id.button_current_list);

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Show current list", Toast.LENGTH_SHORT).show();
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerService == null || !isBound) {
                    Toast.makeText(MainActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isPlaying) {
                    playerService.pause();
                    playPauseButton.setBackgroundResource(R.drawable.ic_play);
                } else {
                    playerService.play();
                    playPauseButton.setBackgroundResource(R.drawable.ic_pause);
                }
                isPlaying = !isPlaying;
            }
        });

        bottomPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });

        View local = findViewById(R.id.ll_local);
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到 LocalMusicActivity
                Intent intent = new Intent(MainActivity.this, LocalMusicActivity.class);
                startActivity(intent);
            }
        });

        View recent = findViewById(R.id.ll_recent);
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到 RecentMusicActivity
                Intent intent = new Intent(MainActivity.this, RecentMusicActivity.class);
                startActivity(intent);
            }
        });

        View favorite = findViewById(R.id.ll_favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到 FavoriteActivity
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        View createFolder = findViewById(R.id.ll_create_folder);
        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 弹出模态框，用户输入歌单名称
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_create_playlist, null);
                builder.setView(dialogView);

                // 2. 输入框包括确认、取消按钮以及输入框
                final EditText playlistNameInput = dialogView.findViewById(R.id.et_playlist_name);

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 3. 点击确认后，创建歌单
                        String playlistName = playlistNameInput.getText().toString().trim();
                        if (!playlistName.isEmpty()) {
                            createPlaylist(playlistName);
                        } else {
                            Toast.makeText(MainActivity.this, "歌单名称不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
                playerService = binder.getService();
                isBound = true;
                // Set initial playlist
                playerService.initMusicList(musicList);
                playerService.setPlaylist(musicList);
                refreshContent();
                refreshPlaylist();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void createPlaylist(String playlistName) {
        // 在这里添加创建歌单的逻辑
        Toast.makeText(this, "歌单 \"" + playlistName + "\" 已创建", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            Folder add = new Folder(playlistName);
            folderDao.insert(add);
//            folderList.add(add);
            refreshPlaylist();
        }).start();
    }

    private void refreshPlaylist() {
        runOnUiThread(() -> {
            RecyclerView recyclerView = findViewById(R.id.folder_recycler_view);
            // TODO: 创建适配器
            FolderAdapter folderAdapter = new FolderAdapter(folderList);
            folderAdapter.setOnItemClickListener(MainActivity.this::onItemClick);
            recyclerView.setAdapter(folderAdapter);
        });
    }

    private void refreshContent() {
        if (isBound) {
            isPlaying = playerService.isPlaying();
            Button playPauseButton = findViewById(R.id.button_play_pause);
            playPauseButton.setBackgroundResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
            TextView title = findViewById(R.id.music_title);
            title.setText(playerService.getCurrentMusic().getTitle());
            TextView artist = findViewById(R.id.music_artist);
            artist.setText(playerService.getCurrentMusic().getArtist());
        }
    }

    private void initDataBase() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "music-db").build();
        folderDao = db.folderDao();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        unregisterReceiver(playStateReceiver);
    }

    private class PlayStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "PLAY_MUSIC_CHANGED")) {
                refreshContent();
            }
        }
    }

    public void onItemClick(Music music) {
        // 处理点击事件
//        if (isBound) {
//            playerService.playMusic(music); // 调用Service的方法
//            Intent intent = new Intent(this, PlayerActivity.class);
//            startActivity(intent);
//        }
        Toast.makeText(playerService, "test", Toast.LENGTH_SHORT).show();
    }
}