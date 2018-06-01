package xx.demo.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;

public class ContentProviderActivity extends BaseActivity
{
    @Override
    public void createChildren(FrameLayout parent, FrameLayout.LayoutParams params)
    {

    }

    private static final String[] FOLDER_PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            "bucket_id",
            "bucket_display_name",
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            "count(*)"};// 最后一个是 COUNT 聚集函数是用来计算一个数据库表中的行数

    @Override
    public void onCreateFinal()
    {
        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                FOLDER_PROJECTION, "1 = 1) GROUP BY (bucket_display_name ", null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        if (cursor != null)
        {
            if (cursor.moveToNext())
            {
                cursor.moveToFirst();

                do
                {
//                    String id = cursor.getString(0);
//                    String data = cursor.getString(1);
//                    String size = cursor.getString(2);
//                    String display_name = cursor.getString(3);
//                    String mime_type = cursor.getString(4);
//                    String title = cursor.getString(5);
//                    String date_added = cursor.getString(6);
//                    String date_modified = cursor.getString(7);
//                    String description = cursor.getString(8);
//                    String picasa_id = cursor.getString(9);
//                    String isPrivate = cursor.getString(10);
//                    String latitude = cursor.getString(11);
//                    String longitude = cursor.getString(12);
//                    String dateTaken = cursor.getString(13);
//                    String orientation = cursor.getString(14);
//                    String mini_thumb_magic = cursor.getString(15);
//                    String bucketId = cursor.getString(16);
//                    String bucket_display_name = cursor.getString(17);
//                    String width = cursor.getString(18);
//                    String height = cursor.getString(19);
//
//                    Info info = new Info(id, data, size, display_name, mime_type, title, date_added,
//                            date_modified, description, picasa_id, isPrivate, latitude, longitude, dateTaken,
//                            orientation, mini_thumb_magic, bucketId, bucket_display_name, width, height);
//
//                    Log.d("xxx", "ContentProviderActivity --> onCreateFinal: info = " + info.toString());
                }
                while (cursor.moveToNext());
            }
        }
    }

    public static class Info
    {
        String id;
        String data;
        String size;
        String display_name;
        String mime_type;
        String title;
        String date_added;
        String date_modified;
        String description;
        String picasa_id;
        String isPrivate;
        String latitude;
        String longitude;
        String dateTaken;
        String orientation;
        String mini_thumb_magic;
        String bucketId;
        String bucket_display_name;
        String width;
        String height;

        public Info(String id, String data, String size, String display_name, String mime_type, String title, String date_added, String date_modified, String description, String picasa_id, String isPrivate, String latitude, String longitude, String dateTaken, String orientation, String mini_thumb_magic, String bucketId, String bucket_display_name, String width, String height)
        {
            this.id = id;
            this.data = data;
            this.size = size;
            this.display_name = display_name;
            this.mime_type = mime_type;
            this.title = title;
            this.date_added = date_added;
            this.date_modified = date_modified;
            this.description = description;
            this.picasa_id = picasa_id;
            this.isPrivate = isPrivate;
            this.latitude = latitude;
            this.longitude = longitude;
            this.dateTaken = dateTaken;
            this.orientation = orientation;
            this.mini_thumb_magic = mini_thumb_magic;
            this.bucketId = bucketId;
            this.bucket_display_name = bucket_display_name;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString()
        {
            return "Info{" +
                    "id='" + id + '\'' +
                    ", data='" + data + '\'' +
                    ", size='" + size + '\'' +
                    ", display_name='" + display_name + '\'' +
                    ", mime_type='" + mime_type + '\'' +
                    ", title='" + title + '\'' +
                    ", date_added='" + date_added + '\'' +
                    ", date_modified='" + date_modified + '\'' +
                    ", description='" + description + '\'' +
                    ", picasa_id='" + picasa_id + '\'' +
                    ", isPrivate='" + isPrivate + '\'' +
                    ", latitude='" + latitude + '\'' +
                    ", longitude='" + longitude + '\'' +
                    ", dateTaken='" + dateTaken + '\'' +
                    ", orientation='" + orientation + '\'' +
                    ", mini_thumb_magic='" + mini_thumb_magic + '\'' +
                    ", bucketId='" + bucketId + '\'' +
                    ", bucket_display_name='" + bucket_display_name + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    '}';
        }
    }
}
