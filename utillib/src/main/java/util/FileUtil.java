package util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwq on 2015/04/15 16:20.<br/><br/>
 * 文件操作工具类
 */
public class FileUtil
{

    private static final String TAG = FileUtil.class.getName();
    private static String sdPath;
    private static String appPath;

    /**
     * res目录下的文件id
     */
    public static int getResId(Context context, String resType, String resName) {
        return context.getResources().getIdentifier(resName, resType, context.getPackageName());
    }

    public static int getLayoutId(Context context, String resName) {
        return getResId(context, "layout", resName);
    }

    public static int getStringId(Context context, String resName) {
        return getResId(context, "string", resName);
    }

    public static int getDrawableId(Context context, String resName) {
        return getResId(context, "drawable", resName);
    }

    public static int getStyleId(Context context, String resName) {
        return getResId(context, "style", resName);
    }

    public static int getId(Context context, String resName) {
        return getResId(context, "id", resName);
    }

    public static int getColorId(Context context, String resName) {
        return getResId(context, "color", resName);
    }

    /**
     * 流转换成图片
     *
     * @param is
     * @param outPadding
     * @param opts
     * @return
     */
    public static Bitmap stream2Bitmap(InputStream is, Rect outPadding, Options opts) {
        Bitmap bitmap = null;
        if (is != null) {
            if (outPadding == null || opts == null) {
                bitmap = BitmapFactory.decodeStream(is);
            } else {
                bitmap = BitmapFactory.decodeStream(is, outPadding, opts);
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            is = null;
        } else {
            Log.i(TAG, "InputStream is null");
        }
        return bitmap;
    }

    public static Bitmap stream2Bitmap(InputStream is) {
        return stream2Bitmap(is, null, null);
    }

    public static String stream2String(InputStream is) {
        return stream2String(is, true);
    }

    public static String stream2String(InputStream is, boolean close) {
        return stream2String(is, close, false);
    }

    public static String stream2String(InputStream is, boolean close, boolean useReader) {
        return stream2String(is, "UTF-8", close, useReader);
    }

    /**
     * 流转换成文本
     *
     * @param is
     * @param charset
     * @param close   使用完后是否关闭流
     * @return
     */
    public static String stream2String(InputStream is, String charset, boolean close, boolean useReader) {
        StringBuffer sb = null;
        try {
            if (is != null) {
                if (charset == null || charset.trim().equals("")) {
                    charset = "UTF-8";
                }
                sb = new StringBuffer();
                String str = null;

                if (useReader) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    if (br != null) {
                        while ((str = br.readLine()) != null) {
                            sb.append(str);
                        }
                        br.close();
                        br = null;
                    }

                } else {
                    byte[] buf = new byte[4096];
                    int len = 0;
                    while ((len = is.read(buf, 0, buf.length)) != -1) {
                        str = new String(buf, 0, len, charset);
                        sb.append(str);
                    }
                    buf = null;
                }

                str = null;
            } else {
                Log.i(TAG, "InputStream is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IOException");
        } finally {
            if (close && is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
        }
        if (sb == null) {
            Log.i(TAG, "StringBuffer is null");
            return null;
        }
        return sb.toString();
    }

    /**
     * 获取Assets资源文件流
     */
    public static InputStream getAssetsStream(Context context, String resName) {
        if (resName == null || resName.trim().equals("")) {
            Log.i(TAG, "resName is null or empty");
            return null;
        }
        AssetManager asset = context.getAssets();
        InputStream is = null;
        try {
            is = asset.open(resName);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IOException");
        }
        return is;
    }

    public static byte[] getAssetsByte(Context context, String resName) {
        InputStream is = FileUtil.getAssetsStream(context, resName);
        if (is != null) {
            byte[] data = null;
            try {
                int len = is.available();
                data = new byte[len];
                is.read(data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
            return data;
        }
        return null;
    }

    /**
     * 获取Assets目录下文件对应的Uri，已带‘/’
     */
    public static String getAssetsFileUri(String fileName) {
        if (isNullOrEmpty(fileName)) return null;
        return "file:///android_asset/" + fileName;
    }

    /**
     * 获取Assets中的图片资源
     */
    public static Bitmap getAssetsBitmap(Context context, String resName) {
        InputStream is = getAssetsStream(context, resName);
        Bitmap bitmap = stream2Bitmap(is);
        return bitmap;
    }

    public static BitmapDrawable getAssetsBitmapDrawable(Context context, String resName) {
        return new BitmapDrawable(getAssetsBitmap(context, resName));
    }

    /**
     * 获取Assets中文本类型资源内容
     */
    public static String getAssetsString(Context context, String resName) {
        InputStream is = getAssetsStream(context, resName);
        String str = stream2String(is);
        return str;
    }

    public static String getAssetsString(Context context, String resName, boolean useReader) {
        InputStream is = getAssetsStream(context, resName);
        String str = stream2String(is, true, useReader);
        return str;
    }

    /**
     * 根据Uri获取文件的真正路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPathFromUri(Context context, Uri uri) {
        if (uri == null)
            return null;

        String fileName = uri.getLastPathSegment();
//        Log.i(TAG, ""+fileName);
        if (fileName != null) {
            String path = uri.getPath();
            if (fileName.lastIndexOf(".") != -1) {
                //real path
                return path;
            } else if (path != null) {
                //图片、视频、音频
                //image video audio
                String columnName = null;
                if (path.contains("image")) {
                    Log.i(TAG, "/*image*/");
                    columnName = MediaStore.Images.Media.DATA;
                } else if (path.contains("video")) {
                    Log.i(TAG, "/*video*/");
                    columnName = MediaStore.Video.Media.DATA;
                } else if (path.contains("audio")) {
                    Log.i(TAG, "/*audio*/");
                    columnName = MediaStore.Audio.Media.DATA;
                } else {
                    //未知类型
                    return path;
                }
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (columnName != null && cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndexOrThrow(columnName);
                    path = cursor.getString(index);
                    cursor.close();
                    cursor = null;
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * SD卡存在并可以使用
     */
    public static boolean isSDExists() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 文件是否存在
     */
    public static boolean isFileExists(String path) {
        return !(isNullOrEmpty(path) || !new File(path).exists());
    }

    /**
     * 获取SD卡根目录，带'/'
     *
     * @return
     */
    public static String getSDPath() {
        if (sdPath == null && isSDExists()) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        }
        return sdPath;
    }

    /**
     * 获取应用在SD卡中的目录，带'/'
     *
     * @param context
     * @return
     */
    public static String getAppPath(Context context) {
        if (appPath == null) {
            String packageName = context.getPackageName();
            appPath = getSDPath() + packageName.substring(packageName.lastIndexOf(".") + 1) + File.separator;
        }
        return appPath;
    }

    /**
     * 获取系统相册目录，‘/’
     *
     * @return
     */
    public static String getCameraPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/";
    }

    /**
     * 获取SD卡的剩余容量，单位是Byte
     *
     * @return
     */
    public static long getSDFreeMemory() {
        if (isSDExists()) {
            File pathFile = Environment.getExternalStorageDirectory();
            // Retrieve overall information about the space on a filesystem.
            // This is a Wrapper for Unix statfs().
            StatFs statfs = new StatFs(pathFile.getPath());
            // 获取SDCard上每一个block的SIZE
            long nBlockSize = statfs.getBlockSize();
            // 获取可供程序使用的Block的数量
            // long nAvailBlock = statfs.getAvailableBlocksLong();
            long nAvailBlock = statfs.getAvailableBlocks();
            // 计算SDCard剩余大小Byte
            long nSDFreeSize = nAvailBlock * nBlockSize;
            return nSDFreeSize;
        }
        return 0;
    }

    /**
     * 获取SD卡的总容量，单位是Byte
     *
     * @return
     */
    public static long getSDMemory() {
        if (isSDExists()) {
            File pathFile = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(pathFile.getPath());
            // 获取SDCard上每一个block的SIZE
            long nBlockSize = statfs.getBlockSize();
            // 获取可供程序使用的Block的数量
            // long nCountBlock = statfs.getBlockCountLong();
            long nCountBlock = statfs.getBlockCount();
            // 计算SDCard剩余大小Byte
            long nSDFreeSize = nCountBlock * nBlockSize;
            return nSDFreeSize;
        }
        return 0;
    }

    /**
     * 获取sd卡文件
     */
    public static InputStream getSDStream(String filePath) {
        if (isNullOrEmpty(filePath)) {
            Log.i(TAG, "filePath is null or empty");
            return null;
        }
        File file = new File(filePath);
        InputStream is = null;
        if (!file.exists()) {
            //不存在
            Log.i(TAG, "filePath not exists");
            return null;
        }
        if (file.isDirectory()) {
            //是目录 返回null
            Log.i(TAG, "filePath is directory");
            return null;
        }
        file = null;
        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "FileNotFoundException");
        }
        return is;
    }

    /**
     * 读取文件 返回数据流
     *
     * @param path
     * @param name
     * @return
     */
    public static InputStream getSDStream(String path, String name) {
        name = searchSDFile(path, name);
        return getSDStream(name);
    }

    /**
     * 遍历文件夹找文件
     */
    public static String searchSDFile(File[] files, String name) {
        String str = null;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                str = searchSDFile(file.listFiles(), name);
                if (str != null) {
                    return str;
                }
            } else {
                if (name.equals(file.getName())) {
                    return file.getAbsolutePath();
                }
            }
        }
        return str;
    }

    /**
     * 遍历文件夹找文件
     */
    public static String searchSDFile(String path, String name) {
        if (isNullOrEmpty(name)) {
            Log.i(TAG, "name is null or empty");
            return null;
        }
        if (isNullOrEmpty(path) || !path.trim().startsWith("/")) {
            Log.i(TAG, "path is null or empty or not startsWith '/'");
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            //不存在
            Log.i(TAG, "path not exists");
            return null;
        }
        if (!file.isDirectory()) {
            //是文件 返回null
            Log.i(TAG, "path is file");
            return null;
        }
        return searchSDFile(file.listFiles(), name);
    }

    /**
     * 根据后缀名查找文件，suffix为空时返回所有文件
     *
     * @param files
     * @param suffix
     * @param fileList
     */
    public static void getSDFiles(File[] files, String suffix, List<String> fileList) {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                getSDFiles(file.listFiles(), suffix, fileList);
            } else {
                if (suffix == null) {
                    fileList.add(file.getAbsolutePath());
                } else {
                    if (file.getName().endsWith(suffix)) {
                        fileList.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * 遍历指定目录下的文件
     *
     * @param file
     * @param suffix 后缀
     * @return
     */
    public static List<String> getFileList(File file, String suffix) {
        if (file == null) {
            Log.i(TAG, "file is null");
            return null;
        }
        List<String> fileList = null;
        if (file.exists()) {
            if (isNullOrEmpty(suffix)) {
                suffix = null;
            } else {
                if (!suffix.startsWith(".")) {
                    suffix = "." + suffix;
                }
            }
            fileList = new ArrayList<String>();
            File[] files = file.listFiles();

            getSDFiles(files, suffix, fileList);
        }
        return fileList;
    }

    /**
     * 获取后缀名为 suffix 的文件所有文件
     *
     * @param path
     * @param suffix 后缀名
     * @return
     */
    public static List<String> getFileList(String path, String suffix) {
        if (isNullOrEmpty(path)) {
            Log.i(TAG, "path is null or empty");
            return null;
        }
        return getFileList(new File(path), suffix);
    }

    /**
     * 获取sd卡中的图片
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSDBitmap(String filePath) {
        InputStream is = getSDStream(filePath);
        Bitmap bitmap = stream2Bitmap(is);
        return bitmap;
    }

    public static Bitmap getSDBitmap(String path, String name) {
        InputStream is = getSDStream(path, name);
        Bitmap bitmap = stream2Bitmap(is);
        return bitmap;
    }

    /**
     * 获取sd卡中文本文件的内容
     *
     * @param filePath
     * @return
     */
    public static String getSDString(String filePath) {
        InputStream is = getSDStream(filePath);
        String str = stream2String(is);
        return str;
    }

    public static String getSDString(String path, String name) {
        InputStream is = getSDStream(path, name);
        String str = stream2String(is);
        return str;
    }

    /**
     * 删除SD卡中的文件或目录
     *
     * @param path
     * @return
     */
    public static boolean deleteSDFile(String path) {
        return deleteSDFile(path, false);
    }

    /**
     * 删除SD卡中的文件或目录
     *
     * @param path
     * @param deleteParent true为删除父目录
     * @return
     */
    public static boolean deleteSDFile(String path, boolean deleteParent) {
        if (isNullOrEmpty(path)) {
            Log.i(TAG, "path is null or empty");
            return false;
        }

        File file = new File(path);
        if (!file.exists()) {
            //不存在
            Log.i(TAG, "path not exists");
            return true;
        }
        return deleteFile(file, deleteParent);
    }

    /**
     * @param file
     * @param deleteParent true为删除父目录
     * @return
     */
    public static boolean deleteFile(File file, boolean deleteParent) {
        boolean flag = false;
        if (file == null) {
            Log.i(TAG, "file is null");
            return flag;
        }
        if (file.isDirectory()) {
            //是文件夹
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    flag = deleteFile(files[i], true);
                    if (!flag) {
                        return flag;
                    }
                }
            }
            if (deleteParent) {
                flag = file.delete();
            }
        } else {
            flag = file.delete();
        }
        file = null;
        return flag;
    }

    /**
     * 把流数据写到SD卡
     *
     * @param is
     * @param path
     * @param deleteOld
     * @param close     关闭stream
     * @return
     */
    public static boolean write2SD(InputStream is, String path, boolean deleteOld, boolean close) {
        if (is == null || isNullOrEmpty(path)) {
            Log.i(TAG, "InputStream is null or path is (null or empty)");
            return false;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            Log.i(TAG, "path is directory");
            return false;
        }
        if (file.exists()) {
            if (deleteOld) {
                file.delete();
            } else {
                return true;
            }
        } else {
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    Log.i(TAG, "path's parent not exists");
                    return false;
                }
            }
        }
        try {
            if (!file.createNewFile()) {
                Log.i(TAG, "file(path) create new file error");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IOException");
            return false;
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            byte[] buf = new byte[4096];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "FileNotFoundException");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IOException");
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                os = null;
            }
            if (close && is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
        }
        return true;
    }

    /**
     * 将文本写入SD卡文件
     *
     * @param content
     * @param path
     * @param deleteOld
     * @return
     */
    public static boolean write2SD(String content, String path, boolean deleteOld) {
        if (isNullOrEmpty(content)) {
            return false;
        }
        return write2SD(content.getBytes(), path, deleteOld);
    }

    public static boolean write2SD(Bitmap bitmap, String path, boolean deleteOld) {
        return write2SD(bitmap, path, deleteOld, true);
    }

    public static boolean write2SD(Bitmap bitmap, String path, boolean deleteOld, boolean needRecycle) {
        if (bitmap == null || bitmap.isRecycled()) {
            return false;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Bitmap temp = null;
//        Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Configure.ARGB_8888);
//        Canvas canvas = new Canvas(temp);
//        canvas.drawBitmap(bitmap, 0, 0, null);
//        temp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        canvas = null;
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        if (needRecycle && bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (temp != null) {
            temp.recycle();
            temp = null;
        }
        return write2SD(data, path, deleteOld);
    }

    public static boolean write2SD(byte[] data, String path, boolean deleteOld) {
        return write2SD(data, 0, -1, path, deleteOld, false);
    }

    public static boolean write2SD(byte[] data, int offset, int count, String path, boolean deleteOld, boolean append) {
        if (data == null) {
            return false;
        }
        if (isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            Log.i(TAG, "path is directory");
            return false;
        } else {
            if (file.exists()) {
                if (deleteOld) {
                    if (!file.delete()) {
                        return false;
                    }
                }
            } else {
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        return false;
                    }
                }
            }
        }
        if (count == -1 || count > data.length) {
            count = data.length;
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(file, append);
            os.write(data, offset, count);
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "FileNotFoundException");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "IOException");
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                os = null;
            }
        }
        return true;
    }


    /**
     * 从assets目录拷贝到手机存储目录
     *
     * @param context
     * @param path
     * @param sdPath    文件的完整目录(包含文件名)
     * @param deleteOld
     * @return
     */
    public static boolean assets2SD(Context context, String path, String sdPath, boolean deleteOld) {
        InputStream is = getAssetsStream(context, path);
        return write2SD(is, sdPath, deleteOld, true);
    }

    /**
     * @param src 源文件目录
     * @param des 目标文件目录
     * @return
     */
    public static boolean copySDFile(String src, String des) {
        InputStream is = getSDStream(src);
        return write2SD(is, des, true, true);
    }

    /**
     * 更改文件夹或文件 名
     *
     * @param src
     * @param des
     * @return
     */
    public static boolean renameFile(String src, String des) {
        boolean success = false;
        if (isNullOrEmpty(src) || isNullOrEmpty(des)) {
            return success;
        }
        File srcFile = new File(src);
        File desFile = new File(des);
        if (srcFile.exists()) {
            //同为目录或同为文件才能重命名
            if ((srcFile.isDirectory() && desFile.isDirectory()) || (srcFile.isFile() && desFile.isFile())) {
                success = srcFile.renameTo(desFile);
            }
        }
        srcFile = null;
        desFile = null;
        return success;
    }

    /**
     * 获取文件的MD5值
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            Log.i(TAG, "file is null or file not exists or file isn't a File");
            return null;
        }
        MessageDigest digest = null;
        InputStream is = null;
        byte buffer[] = new byte[4096];
        int len = 0;
        try {
            digest = MessageDigest.getInstance("MD5");
            is = new FileInputStream(file);
            while ((len = is.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Exception");
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 获取文件的MD5值
     *
     * @param filepath
     * @return
     */
    public static String getFileMD5(String filepath) {
        if (isNullOrEmpty(filepath)) {
            Log.i(TAG, "filepath is null or empty");
            return null;
        }
        File file = new File(filepath);
        return getFileMD5(file);
    }

    private static boolean isNullOrEmpty(String content) {
        return content == null || content.trim().isEmpty();
    }

    /**
     * 获取本地视频的第一帧
     *
     * @param filePath
     * @return
     */
    public static Bitmap getLocalVideoThumbnail(String filePath, boolean ARGB_8888) {
        if (TextUtils.isEmpty(filePath)) return null;
        Bitmap bitmap = null;
        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
        //的接口，用于从输入的媒体文件中取得帧和元数据；
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(filePath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
            retriever = null;
        }
        if (ARGB_8888 && bitmap != null && !bitmap.isRecycled()) {
            if (bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                Bitmap temp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmap.recycle();
                bitmap = temp;
                temp = null;
            }
        }
        return bitmap;
    }

    public static Bitmap getLocalVideoThumbnail(String filePath) {
        return getLocalVideoThumbnail(filePath, false);
    }

    //通过路径加载Assets中的文本内容
    public static String getAssetsResource(Resources mRes, String path)
    {
        StringBuilder result = new StringBuilder();
        try
        {
            InputStream is = mRes.getAssets().open(path);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer)))
            {
                result.append(new String(buffer, 0, ch));
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }
}
