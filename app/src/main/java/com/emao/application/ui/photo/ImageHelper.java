package com.emao.application.ui.photo;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageHelper {
	public static final String TAG = "ImageHelper";
	private static ImageHelper INSTANCE;
	private Context mContext;

    private List<ImageBucket> mLocalBucketList;

    private long mCachedBucketId = -1;
	private List<ImageItem> mCachedImageList;

	private static final Comparator<ImageBucket> BUCKETS_SORTER = new Comparator<ImageBucket>() {
		
		@Override
		public int compare(ImageBucket bucket0, ImageBucket bucket1) {
            if(bucket0.getId() == 0) {
                return -1;
            }
            if(bucket1.getId() == 0) {
                return 1;
            }
			if(bucket0.getDateModified() < bucket1.getDateModified()) {
				return 1;
			}
			return -1;
		}
	};
	
	private static final Comparator<ImageItem> IMAGES_SORTER = new Comparator<ImageItem>() {
		
		@Override
		public int compare(ImageItem img0, ImageItem img1) {
			if(img0.getDateAdded() < img1.getDateAdded()) {
				return 1;
			}
			return -1;
		}
	};
	
	private ImageHelper(Context context) {
//        mContext = context;
        mContext = context.getApplicationContext();

        mLocalBucketList = new ArrayList<ImageBucket>();
        mCachedImageList = new ArrayList<ImageItem>();
//		initData();
	}
	
	//TODO: 下一步需要考虑线程安全的问题
	public static synchronized ImageHelper getInstance(Context context) {
		if(null == INSTANCE) {
			INSTANCE = new ImageHelper(context);
		}
		return INSTANCE;
	}

	private void initData() {
        if(mLocalBucketList.size() > 0) {
            mLocalBucketList.clear();
        }
        ImageBucket allInOneBucket = new ImageBucket();
        allInOneBucket.setId(0);
        allInOneBucket.setDisplayName("所有图片");

        int totalCount = 0;
        Cursor cursor = null;
        try {
            String selection = "0==0) GROUP BY (" + Media.BUCKET_ID;
            cursor = mContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, ImageBucket.BUCKET_PROJECTION, selection, null, Media.DATE_ADDED + " DESC");
            while(cursor.moveToNext()) {
                ImageBucket bucket = new ImageBucket();
                bucket.loadFromCursor(cursor);
                totalCount += bucket.getCount();
                mLocalBucketList.add(bucket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        if(totalCount > 0) {
            ImageBucket firstBucket = mLocalBucketList.get(0);
            allInOneBucket.setPreviewPath(firstBucket.getPreviewPath());
            allInOneBucket.setDateModified(firstBucket.getDateModified());
        }
        allInOneBucket.setCount(totalCount);
        mLocalBucketList.add(0, allInOneBucket);
	}
	
	public List<ImageBucket> getLocalBucketList(boolean refresh) {
		if(refresh) {
            mLocalBucketList.clear();
            mCachedImageList.clear();
            mCachedBucketId = -1;
			initData();
		}
		
		List<ImageBucket> buckets = new ArrayList<ImageBucket>();
		buckets.addAll(mLocalBucketList);
		Collections.sort(buckets, BUCKETS_SORTER);
		return buckets;
	}
	
	public List<ImageBucket> getLocalBucketList() {
		return getLocalBucketList(false);
	}
	
	public long findIdByBucketName(String name) {
		long id = 0;
		if(mLocalBucketList.size() > 0) {
			id = mLocalBucketList.get(0).getId();
			for (ImageBucket bucket : mLocalBucketList) {
				if(bucket.getDisplayName().equals(name)) {
					id = bucket.getId();
				}
			}
		}
		return id;
	}
	
	public long findDefaultBucketId() {
		return findIdByBucketName("Camera");
	}
	
	public ImageBucket getBucket(long id) {
        for(ImageBucket bucket : mLocalBucketList) {
            if(bucket.getId() == id) {
                return  bucket;
            }
        }
		return null;
	}

    private List<ImageItem> getAllLocalPhotos() {
        mCachedBucketId = 0;
        List<ImageItem> photos = new ArrayList<ImageItem>();
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, ImageItem.IMAGE_PROJECTION, null, null, Media.DATE_ADDED + " DESC");
            while(cursor.moveToNext()) {
                ImageItem imageItem = new ImageItem();
                imageItem.loadFromCursor(cursor);
                photos.add(imageItem);
                mCachedImageList.add(imageItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCachedBucketId = -1;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

//		Collections.sort(photos, IMAGES_SORTER);
        return photos;
    }
	
	public List<ImageItem> getLocalPhotosByBucketId(long id) {
        mCachedImageList.clear();
        if(id == 0) {
            return getAllLocalPhotos();
        }
        mCachedBucketId = id;
        List<ImageItem> photos = new ArrayList<ImageItem>();
        Cursor cursor = null;
        try {
            String selection = Media.BUCKET_ID + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(id)};
            cursor = mContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, ImageItem.IMAGE_PROJECTION, selection, selectionArgs, Media.DATE_ADDED + " DESC");
            while(cursor.moveToNext()) {
                ImageItem imageItem = new ImageItem();
                imageItem.loadFromCursor(cursor);
                photos.add(imageItem);
                mCachedImageList.add(imageItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mCachedBucketId = -1;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

//		Collections.sort(photos, IMAGES_SORTER);
		return photos;
	}

	public List<ImageItem> getImagesByBucketId(long bucketId) {
        List<ImageItem> cachedImages = new ArrayList<ImageItem>();
        if(mCachedBucketId == bucketId) {
            cachedImages.addAll(mCachedImageList);
        }
        else {
            cachedImages.addAll(getLocalPhotosByBucketId(bucketId));
        }
        return cachedImages;
	}

    public List<ImageItem> getImagesById(long[] ids) {
        List<ImageItem> images = new ArrayList<ImageItem>();
        if(ids != null && ids.length > 0) {
            Map<Long, ImageItem> tempMap = new HashMap<Long, ImageItem>();
            String inStr = "" + ids[0];
            for(int i = 1; i < ids.length; i++) {
                inStr += "," + ids[i];
            }
            Cursor cursor = null;
            try {
                String selection = Media._ID + " IN (" + inStr + ")";
                cursor = mContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, ImageItem.IMAGE_PROJECTION, selection, null, Media.DATE_ADDED + " DESC");
                while (cursor.moveToNext()) {
                    ImageItem imageItem = new ImageItem();
                    imageItem.loadFromCursor(cursor);
                    tempMap.put(imageItem.getId(), imageItem);
                }
                for(int i = 0; i < ids.length; i++) {
                    ImageItem imageItem = tempMap.get(ids[i]);
                    if(imageItem != null) {
                        images.add(imageItem);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
            }
        }

        return images;
    }

}
