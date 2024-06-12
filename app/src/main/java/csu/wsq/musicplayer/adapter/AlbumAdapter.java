package csu.wsq.musicplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.entity.Album;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Album> albumList;

    public AlbumAdapter(List<Album> albumList) {
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.titleTextView.setText(album.getTitle());
        holder.artistTextView.setText(album.getArtist());
//        holder.songCountTextView.setText(context.getString(R.string.song_count, album.getSongCount()));
//        holder.songCountTextView.setText(album.getSongCount());
//        holder.durationTextView.setText(album.getDuration());

        // TODO:使用Glide加载专辑封面图片
//        Glide.with(context)
//                .load(album.getCoverImageUrl())
//                .placeholder(R.drawable.placeholder_image) // 占位符图片
//                .into(holder.coverImageView);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView;
        TextView artistTextView;
        TextView songCountTextView;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.album_cover_image);
            titleTextView = itemView.findViewById(R.id.album_title);
            artistTextView = itemView.findViewById(R.id.album_artist);
            songCountTextView = itemView.findViewById(R.id.album_song_count);
        }
    }
}