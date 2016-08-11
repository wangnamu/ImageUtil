package com.ufo.imageutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by tjpld on 16/6/21.
 */
public class CCImageHelper {

    private static CCImageHelper mCCImageHelper;
    private String mPath;
    private CCImageHelperConfig mCCImageHelperConfig;
    private Bitmap mBitmap;


    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public CCImageHelperConfig getmCCImageHelperConfig() {
        return mCCImageHelperConfig;
    }

    public void setmCCImageHelperConfig(CCImageHelperConfig imageHelperConfig) {
        this.mCCImageHelperConfig = imageHelperConfig;
    }

    protected void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }


    /**
     * 读取图片并压缩
     *
     * @param path              图片路径
     * @param imageHelperConfig 配置信息
     * @return CCImageHelper
     */
    public static CCImageHelper loadWithCompress(String path, CCImageHelperConfig imageHelperConfig) {


        mCCImageHelper = new CCImageHelper();
        mCCImageHelper.setPath(path);
        mCCImageHelper.setmCCImageHelperConfig(imageHelperConfig);
        mCCImageHelper.setmBitmap(mCCImageHelper.getBitmapFromPathWithCompressBySize(path));

        return mCCImageHelper;
    }


    /**
     * 读取图片
     *
     * @param path              图片路径
     * @param imageHelperConfig 配置信息
     * @return CCImageHelper
     */
    public static CCImageHelper load(String path, CCImageHelperConfig imageHelperConfig) {

        mCCImageHelper = new CCImageHelper();
        mCCImageHelper.setPath(path);
        mCCImageHelper.setmCCImageHelperConfig(imageHelperConfig);
        mCCImageHelper.setmBitmap(mCCImageHelper.getmBitmapFromPath(path));

        return mCCImageHelper;
    }


    /**
     * 质量压缩
     *
     * @return CCImageHelper
     */
    public CCImageHelper quality() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        boolean isCompressed = false;
        while (baos.toByteArray().length / 1024 > mCCImageHelperConfig.getStreamQuality() && quality > 0) {
            quality -= 10;
            baos.reset();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            isCompressed = true;
        }
        if (isCompressed) {
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                    baos.toByteArray(), 0, baos.toByteArray().length);
            recycleBitmap(mBitmap);

            mBitmap = compressedBitmap;
        }

        return mCCImageHelper;
    }


    /**
     * 旋转
     *
     * @return CCImageHelper
     */
    public CCImageHelper rotate() {

        ExifInterface exif;
        Bitmap newBitmap = null;
        try {
            exif = new ExifInterface(mPath);
            if (exif != null) { // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                int digree = 0;
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                }
                if (digree != 0) {
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    newBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                            mBitmap.getWidth(), mBitmap.getHeight(), m, true);
                    recycleBitmap(mBitmap);
                    mBitmap = newBitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mCCImageHelper;
    }


    /**
     * 水印
     * @param mark 水印文字
     * @return CCImageHelper
     */
    public CCImageHelper watermark(String mark) {
        return watermark(mark, null);
    }

    /**
     * 水印
     * @param mark 水印文字
     * @param drawPaintLisenter 回调
     * @return CCImageHelper
     */
    public CCImageHelper watermark(String mark, DrawPaintLisenter drawPaintLisenter) {

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(mCCImageHelperConfig.getWaterMarkFontColor());
        paint.setTextSize(mCCImageHelperConfig.getWaterMarkFontSize());
        paint.setAntiAlias(true);

        canvas.drawBitmap(mBitmap, 0, 0, paint);

        PointF pointF;

        if (drawPaintLisenter != null) {
            pointF = drawPaintLisenter.onDrawText(w, h, paint);
        } else {
            pointF = new PointF();
            pointF.x = w / 2 - paint.measureText(mark) / 2;
            pointF.y = h - mCCImageHelperConfig.getWaterMarkFontSize() / 2;
        }

        canvas.drawText(mark, pointF.x, pointF.y, paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        recycleBitmap(mBitmap);

        mBitmap = bitmap;

        return mCCImageHelper;
    }


    /**
     * 保存到存储空间
     *
     * @param path 保存路径
     * @return 保存路径
     */
    public String saveToStorage(String path) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                mBitmap.compress(Bitmap.CompressFormat.JPEG, mCCImageHelperConfig.getDataQuality(), fos);
                fos.close();
            }
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 返回Bitmap
     *
     * @return Bitmap
     */
    public Bitmap build() {
        return mBitmap;
    }


    /**
     * 从本地路径读取文件
     * @param path 文件路径
     * @return Bitmap
     */
    protected Bitmap getmBitmapFromPath(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }


    /**
     * 从本地路径读取文件并按尺寸压缩
     * @param path 文件路径
     * @return Bitmap
     */
    protected Bitmap getBitmapFromPathWithCompressBySize(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();

        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);//此时返回bm为空

        opts.inJustDecodeBounds = false;

        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;

        int widthRatio = (int) Math.ceil(imgWidth / (float) mCCImageHelperConfig.getWidth());
        int heightRatio = (int) Math.ceil(imgHeight / (float) mCCImageHelperConfig.getHeight());
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }

        opts.inJustDecodeBounds = false;

        Bitmap compressedBitmap = BitmapFactory.decodeFile(path, opts);

        recycleBitmap(bitmap);
        return compressedBitmap;
    }



    /**
     * 回收位图对象
     * @param bitmap bitmap
     */
    protected void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
            bitmap = null;
        }
    }

    /**
     * 水印回调
     */
    public interface DrawPaintLisenter {
        /**
         *
         * @param width 图片宽度
         * @param height 图片高度
         * @param paint 画笔
         * @return 水印在图片中的点位
         */
        PointF onDrawText(int width, int height, Paint paint);
    }


}
