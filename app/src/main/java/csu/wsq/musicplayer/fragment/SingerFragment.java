package csu.wsq.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.adapter.MusicAdapter;
import csu.wsq.musicplayer.entity.Music;

public class SingerFragment extends Fragment {

    private static final String ARG_MUSIC_LIST = "music_list";
    private List<Music> musicList;
    private RecyclerView recyclerView;

    public SingerFragment() {
        // Required empty public constructor
    }

    public static SingerFragment newInstance(List<Music> musicList) {
        SingerFragment fragment = new SingerFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singer, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MusicAdapter(musicList));
        return view;
    }
}