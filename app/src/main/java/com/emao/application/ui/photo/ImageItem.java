package com.emao.application.ui.photo;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Images.ImageColumns;

/**
 * @author keybon
 */
public class ImageItem implements Parcelable {

	private long id;
	private long bucketId;
	private long dateAdded;
	private String path;

    public static final String[] IMAGE_PROJECTION = {
            ImageColumns._ID,
            ImageColumns.BUCKET_ID,
            ImageColumns.DATE_ADDED,
            ImageColumns.DATA
    };

    public ImageItem() {
    }

    protected ImageItem(Parcel in) {
        id = in.readLong();
        bucketId = in.readLong();
        dateAdded = in.readLong();
        path = in.readString();
    }

    public long getId() {
        return id;
    }

    public long getBucketId() {
        return bucketId;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageItem imageItem = (ImageItem) o;

        return id == imageItem.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "id=" + id +
                ", bucketId=" + bucketId +
                ", dateAdded=" + dateAdded +
                ", path='" + path + '\'' +
                '}';
    }

    private static boolean idxLoaded = false;
    private static int idIdx;
    private static int bucketIdIdx;
    private static int dateAddedIdx;
    private static int pathIdx;

    public void loadFromCursor(Cursor cursor) {
        if(!idxLoaded) {
            idIdx = cursor.getColumnIndex(ImageColumns._ID);
            bucketIdIdx = cursor.getColumnIndex(ImageColumns.BUCKET_ID);
            dateAddedIdx = cursor.getColumnIndex(ImageColumns.DATE_ADDED);
            pathIdx = cursor.getColumnIndex(ImageColumns.DATA);
            idxLoaded = true;
        }

        this.id = cursor.getLong(idIdx);
        this.bucketId = cursor.getLong(bucketIdIdx);
        this.dateAdded = fixDateTime(cursor.getLong(dateAddedIdx));
        this.path = cursor.getString(pathIdx);
    }

    private long fixDateTime(long dateTime) {
        if(dateTime > 1000000000000L && dateTime < 9999999999999L) {
            return  dateTime;
        }
        String s = String.valueOf(dateTime);
        return (long) (dateTime / (Math.pow(10, s.length() - 13)));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(bucketId);
        dest.writeLong(dateAdded);
        dest.writeString(path);
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
