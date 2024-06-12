package csu.wsq.musicplayer.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.activity.PlayerActivity;
import csu.wsq.musicplayer.activity.RecentMusicActivity;
import csu.wsq.musicplayer.adapter.MusicAdapter;
import csu.wsq.musicplayer.entity.Music;
import csu.wsq.musicplayer.service.PlayerService;

public class SingleSongFragment extends Fragment {

    private static final String ARG_MUSIC_LIST = "music_list";
    private List<Music> musicList;
    private RecyclerView recyclerView;
    private ServiceConnection serviceConnection;
    private PlayerService playerService;
    private boolean isBound = false;

    public SingleSongFragment() {
        // Required empty public constructor
    }

    public static SingleSongFragment newInstance(List<Music> musicList) {
        SingleSongFragment fragment = new SingleSongFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_MUSIC_LIST, new ArrayList<>(musicList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            musicList = getArguments().getParcelableArrayList(ARG_MUSIC_LIST);
        }
        initService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_song, container, false);
        recyclerView = view.findViewById(R.id.songRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MusicAdapter musicAdapter = new MusicAdapter(musicList);
        musicAdapter.setOnItemClickListener(this::onItemClick);
        recyclerView.setAdapter(musicAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 绑定Service
        Intent intent = new Intent(getContext(), PlayerService.class);
        getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 解绑Service
        if (isBound) {
            getContext().unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void initService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
                playerService = binder.getService();
                isBound = true;
                // Set initial playlist or any other initialization if needed
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
    }

    public void onItemClick(Music music) {
        // 处理点击事件
        if (isBound) {
            playerService.playMusic(music); // 调用Service的方法
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            startActivity(intent);
        }
    }
}