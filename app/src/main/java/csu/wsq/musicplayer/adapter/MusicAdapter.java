package csu.wsq.musicplayer.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.common.OnItemClickListener;
import csu.wsq.musicplayer.dao.FavoriteDao;
import csu.wsq.musicplayer.dao.FavoriteDao_Impl;
import csu.wsq.musicplayer.entity.Favorite;
import csu.wsq.musicplayer.entity.Music;
import csu.wsq.musicplayer.util.AppDatabase;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<Music> musicList;

    private OnItemClickListener onItemClickListener;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public MusicAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        Log.println(Log.INFO, "MusicAdapter", "setOnItemClickListener");
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.titleTextView.setText(music.getTitle());
        holder.artistTextView.setText(music.getArtist());
//        holder.albumTextView.setText(music.getAlbum());
//        holder.durationTextView.setText(music.getDuration());

        holder.bind(music, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        TextView albumTextView;
        TextView durationTextView;
        Button btnOption;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            btnOption =itemView.findViewById(R.id.btnOption);
//            albumTextView = itemView.findViewById(R.id.albumTextView);
//            durationTextView = itemView.findViewById(R.id.durationTextView);
        }

        public void bind(final Music music, final OnItemClickListener listener) {

            btnOption.setOnClickListener(v -> {
                // 这里实现弹出多个选项单页面的逻辑
                showOptionsMenu(v,music,listener);
            });
        }

        public void showOptionsMenu(View view,final Music music, final OnItemClickListener listener) {

              Executor executor = Executors.newSingleThreadExecutor();
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.music_options_menu, popupMenu.getMenu());
            //设置选中
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.option_1) {
                    Toast.makeText(view.getContext(), "播放成功", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onItemClick(music);
                    }
                    return true;
                } else if (id == R.id.option_2) {
                    if(music.isLiked()) {
                        music.setLiked(false);
                        Toast.makeText(view.getContext(), "已经收藏过了", Toast.LENGTH_SHORT).show();
                        executor.execute(() -> favoriteDao.deleteByMusicId(music.getId()));
                    } else {
                        music.setLiked(true);
                        Toast.makeText(view.getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                        executor.execute(() -> favoriteDao.insert(new Favorite(music.getId())));
                    }
                    refreshInfo();
                    return true;
                } else if (id == R.id.option_3) {
                    Toast.makeText(view.getContext(), "选项3被点击", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();



        }


    }
}