package com.example.travellog.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {

    private static final String PHOTO_DIR = "Pictures/TravelLog";
    private static final String VIDEO_DIR = "Videos/TravelLog";
    private static final String AUDIO_DIR = "Audio/TravelLog";
    private static final String RECEIPT_DIR = "Receipts/TravelLog";

    private static final SimpleDateFormat TIMESTAMP_FORMAT =
            new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    public static File createPhotoFile(Context context) {
        File dir = getExternalDir(context, PHOTO_DIR);
        if (!dir.exists()) dir.mkdirs();
        String fileName = "IMG_" + TIMESTAMP_FORMAT.format(new Date()) + ".jpg";
        return new File(dir, fileName);
    }

    public static File createVideoFile(Context context) {
        File dir = getExternalDir(context, VIDEO_DIR);
        if (!dir.exists()) dir.mkdirs();
        String fileName = "VID_" + TIMESTAMP_FORMAT.format(new Date()) + ".mp4";
        return new File(dir, fileName);
    }

    public static File createAudioFile(Context context) {
        File dir = getExternalDir(context, AUDIO_DIR);
        if (!dir.exists()) dir.mkdirs();
        String fileName = "AUD_" + TIMESTAMP_FORMAT.format(new Date()) + ".m4a";
        return new File(dir, fileName);
    }

    public static File createReceiptFile(Context context) {
        File dir = getExternalDir(context, RECEIPT_DIR);
        if (!dir.exists()) dir.mkdirs();
        String fileName = "RCP_" + TIMESTAMP_FORMAT.format(new Date()) + ".jpg";
        return new File(dir, fileName);
    }

    private static File getExternalDir(Context context, String subDir) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (subDir.startsWith("Videos")) {
            dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        } else if (subDir.startsWith("Audio")) {
            dir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        }
        if (dir == null) {
            dir = context.getFilesDir();
        }
        return new File(dir, "TravelLog");
    }

    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static String getFileExtension(String path) {
        if (path == null) return "";
        int dot = path.lastIndexOf('.');
        if (dot >= 0) {
            return path.substring(dot + 1).toLowerCase();
        }
        return "";
    }

    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format(Locale.US, "%.1f %s",
                size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    public static String getMimeType(String filePath) {
        String extension = getFileExtension(filePath);
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "3gp":
                return "video/3gpp";
            case "m4a":
                return "audio/mp4";
            case "mp3":
                return "audio/mpeg";
            case "wav":
                return "audio/wav";
            default:
                return "application/octet-stream";
        }
    }
}
