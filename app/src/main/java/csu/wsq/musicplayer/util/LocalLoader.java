package csu.wsq.musicplayer.util;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import csu.wsq.musicplayer.entity.Album;
import csu.wsq.musicplayer.entity.Music;

public class LocalLoader {
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;
    private final Context context;

    public LocalLoader(Context context) {
        this.context = context;
    }



    public List<Music> loadLocalMusic() {
        List<Music> musicList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        };

        try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    if (data == null || !data.toLowerCase().endsWith(".mp3")) {
                        // Skip this file
                        continue;
                    }

                    long durationMillis = 0;
                    String durationStr = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    if (durationStr != null) {
                        try {
                            durationMillis = Long.parseLong(durationStr);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    String duration = convertDuration(durationMillis);
                    musicList.add(new Music(id, title, album, artist, data, duration));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicList;
    }

    public List<Album> loadLocalAlbums() {
        List<Album> albumList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        };
        try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST));
                    String coverImageUrl = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
                    int songCount = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                    albumList.add(new Album(id, title, artist, coverImageUrl, songCount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumList;
    }

    @SuppressLint("DefaultLocale")
    private String convertDuration(long durationMillis) {
        // Convert duration in milliseconds to a human-readable format (e.g., "1:23:45")
        long hours = durationMillis / (1000 * 60 * 60);
        long minutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (durationMillis % (1000 * 60)) / 1000;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
