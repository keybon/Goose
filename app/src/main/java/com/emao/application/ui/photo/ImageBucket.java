package com.emao.application.ui.photo;

import android.database.Cursor;
import android.provider.MediaStore.Images.ImageColumns;

public class ImageBucket {

    public static final String BucketColumns_Count = "Count";
    public static final String BucketColumns_Date_Modified = "DateModified";
	
	private long id;
	private String previewPath;
	private long dateModified;
	private String displayName;
	private int count;

    public static final String[] BUCKET_PROJECTION = {
            ImageColumns.BUCKET_ID,
            ImageColumns.DATA,
            "MAX("+ ImageColumns.DATE_ADDED + ") AS "+ BucketColumns_Date_Modified,
            ImageColumns.BUCKET_DISPLAY_NAME,
            "COUNT(*) AS " + BucketColumns_Count
    };

    public ImageBucket() {
        this.id = -1;
        this.previewPath = null;
        this.dateModified = 0;
        this.displayName = "empty bucket";
        this.count = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImageBucket bucket = (ImageBucket) o;

        return id == bucket.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ImageBucket{" +
                "id=" + id +
                ", previewPath='" + previewPath + '\'' +
                ", dateModified=" + dateModified +
                ", displayName='" + displayName + '\'' +
                ", count=" + count +
                '}';
    }

    private static boolean idxLoaded = false;
    private static int idIdx;
    private static int previewPathIdx;
    private static int dateModifiedIdx;
    private static int displayNameIdx;
    private static int countIdx;

    public void loadFromCursor(Cursor cursor) {
        if(!idxLoaded) {
            idIdx = cursor.getColumnIndex(ImageColumns.BUCKET_ID);
            previewPathIdx = cursor.getColumnIndex(ImageColumns.DATA);
            dateModifiedIdx = cursor.getColumnIndex(BucketColumns_Date_Modified);
            displayNameIdx = cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME);
            countIdx = cursor.getColumnIndex(BucketColumns_Count);
            idxLoaded = true;
        }

        this.id = cursor.getLong(idIdx);
        this.previewPath = cursor.getString(previewPathIdx);
        this.dateModified = fixDateTime(cursor.getLong(dateModifiedIdx));
        this.displayName = cursor.getString(displayNameIdx);
        this.count = cursor.getInt(countIdx);
    }

    private long fixDateTime(long dateTime) {
        if(dateTime > 1000000000000L && dateTime < 9999999999999L) {
            return  dateTime;
        }
        String s = String.valueOf(dateTime);
        return (long) (dateTime / (Math.pow(10, s.length() - 13)));
    }
}
