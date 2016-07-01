package com.ufo.imageutil;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by tjpld on 16/6/21.
 */
public class CCImageHelperConfig {

    /*
    * 压缩宽度
    */
    private int Width;

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }


    /*
     * 压缩高度
     */
    private int Height;

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }


    /*
     * 流品质
     */
    private int StreamQuality;

    public int getStreamQuality() {
        return StreamQuality;
    }

    public void setStreamQuality(int streamQuality) {
        StreamQuality = streamQuality;
    }


    /*
     * 文件品质
     */
    private int DataQuality;

    public int getDataQuality() {
        return DataQuality;
    }

    public void setDataQuality(int dataQuality) {
        DataQuality = dataQuality;
    }

    /*
     * 水印字体大小
     */
    private float WaterMarkFontSize;

    public float getWaterMarkFontSize() {
        return WaterMarkFontSize;
    }

    public void setWaterMarkFontSize(float waterMarkFontSize) {
        WaterMarkFontSize = waterMarkFontSize;
    }


    private int WaterMarkFontColor;

    public int getWaterMarkFontColor() {
        return WaterMarkFontColor;
    }

    public void setWaterMarkFontColor(int waterMarkFontColor) {
        WaterMarkFontColor = waterMarkFontColor;
    }



    public CCImageHelperConfig() {
        this.Width = 600;
        this.Height = 800;
        this.StreamQuality = 40;
        this.DataQuality = 80;
        this.WaterMarkFontSize = 48.0f;
        this.WaterMarkFontColor = Color.YELLOW;
    }

}
