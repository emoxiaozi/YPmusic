package csu.wsq.musicplayer.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.adapter.ViewPagerAdapter;
import csu.wsq.musicplayer.entity.Album;
import csu.wsq.musicplayer.entity.Music;
import csu.wsq.musicplayer.fragment.*;
import csu.wsq.musicplayer.util.LocalLoader;

public class LocalMusicActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 100;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Music> musicList;
    private List<Album> albumList;
    private LocalLoader localLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        localLoader = new LocalLoader(this);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
//        } else {
//            loadMusicAndSetupViewPager();
//        }
        loadMusicAndSetupViewPager();
    }

    private void loadMusicAndSetupViewPager() {
        musicList = localLoader.loadLocalMusic();
        albumList = localLoader.loadLocalAlbums();
        TextView noContentTextView = findViewById(R.id.noContentTextView);
        if (musicList.isEmpty()) {
            noContentTextView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        } else {
            noContentTextView.setVisibility(View.GONE);
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SingleSongFragment.newInstance(musicList), "Single Song");
        adapter.addFragment(AlbumFragment.newInstance(albumList), "Album");
        adapter.addFragment(SingerFragment.newInstance(musicList), "Singer");
        adapter.addFragment(FolderFragment.newInstance(musicList), "Folder");
        viewPager.setAdapter(adapter);
    }
}