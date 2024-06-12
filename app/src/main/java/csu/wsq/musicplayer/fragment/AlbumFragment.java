package csu.wsq.musicplayer.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.adapter.AlbumAdapter;
import csu.wsq.musicplayer.entity.Album;

public class AlbumFragment extends Fragment {

    private static final String ARG_ALBUM_LIST = "album_list";
    private List<Album> albumList;
    private RecyclerView recyclerView;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance(List<Album> albumList) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ALBUM_LIST, new ArrayList<>(albumList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumList = getArguments().getParcelableArrayList(ARG_ALBUM_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.albumRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AlbumAdapter(albumList));
        return view;
    }
}