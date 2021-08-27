package com.toolsbox.contractor.common.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import androidx.annotation.RequiresApi;

import id.zelory.compressor.Compressor;

public class FileUtils {
    public static final String IMAGE_EXTENSION = ".jpg";
    public static final String AUDIO_EXTENSION = ".mp4";
	/**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath = isExternalStorageWritable() || !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    @TargetApi(VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (Utils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }
    
    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
    	if (Utils.hasFroyo()) {
    		return context.getExternalCacheDir();
    	}

    	// Before Froyo we need to construct the external cache dir ourselves
    	final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
    	return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }


    private static File getImagesDir(Context context) {
        return isExternalStorageWritable() ? context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) : context.getDir("images", Context.MODE_PRIVATE);
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File getSentAudioDir(Context context) {
        File dir = new File(getAudioDir(context), "sent");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static File getReceivedAudioDir(Context context) {
        File dir = new File(getAudioDir(context), "received");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }


    private static File getAudioDir(Context context) {
        return isExternalStorageWritable() ? context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) : context.getDir("musics", Context.MODE_PRIVATE);
    }


    @RequiresApi(api = VERSION_CODES.KITKAT)
    public static String getRealPathFromURI(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public static File compressImage(Context context, File originFile){
        try {
            File compressedImage = new Compressor(context)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(originFile);
            Log.e("fileName=", compressedImage.getName());
            Log.e("filePath=", compressedImage.getAbsolutePath());
            Log.e("size=", "" + compressedImage.length() / 1024 + "KB");
            return compressedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getFile (Context context, Uri uri) {
        String path = getPath(context, uri);
        assert path != null;
        return new File(path);
    }

    static String getPath(Context context, Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory().getPath() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean existFileInChatDirectory(String fileName){
        String strPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "JobsPro" + File.separator + fileName;
        File file = new File(strPath);
        return file.exists();
    }

    public static File getFileInChatDirectory(String fileName) {
        String strPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "JobsPro" + File.separator + fileName;
        return new File(strPath);
    }


    public static File getFileDir(Context context) {
        final String dir = "/JobsPro";
        return new File(Environment.getExternalStorageDirectory().getPath() + dir);
    }

    public static File saveImage(Bitmap bitmap){

        File root = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "JobsPro");
        if (!root.exists())
        {
            root.mkdir();
        }

        Random r = new Random();
        int i1 = (r.nextInt(80) + 65);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + i1 ;
        String strPhoto = Environment.getExternalStorageDirectory().getPath() + File.separator + "JobsPro" + File.separator + "IMG_" + timeStamp + ".jpg";
        File mediaFile = new File(strPhoto);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            FileOutputStream stream = new FileOutputStream(mediaFile);
            stream.write(out.toByteArray());
            stream.close();

        } catch (IOException exception) {
            Log.e("FileUtils", "errorr" + exception.getLocalizedMessage());
            exception.printStackTrace();
            strPhoto="";
        }
        return mediaFile;
    }
}