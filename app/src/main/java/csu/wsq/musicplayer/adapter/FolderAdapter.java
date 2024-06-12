package csu.wsq.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import csu.wsq.musicplayer.R;
import csu.wsq.musicplayer.common.OnItemClickListener;
import csu.wsq.musicplayer.entity.Folder;
import csu.wsq.musicplayer.entity.Music;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<Folder> folderList;
    private OnItemClickListener onItemClickListener;

    public FolderAdapter(List<Folder> folderList) {
        this.folderList = folderList;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        holder.folderName.setText(folder.getName());
        // 设置歌曲数量，如果有这个字段
        holder.musicCount.setText(folder.getMusicCount() + " songs");

        // 使用Glide加载封面图片
//        Glide.with(context)
//                .load(folder.getCoverImageUrl())
//                .placeholder(R.drawable.placeholder_image) // 占位符图片
//                .into(holder.albumCoverImage);
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView albumCoverImage;
        TextView folderName;
        TextView musicCount;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            albumCoverImage = itemView.findViewById(R.id.album_cover_image);
            folderName = itemView.findViewById(R.id.folder_name);
            musicCount = itemView.findViewById(R.id.music_count);
        }
    }
}