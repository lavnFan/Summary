package com.aliyun.ushell;

/**
 * Created by wufan on 2016/7/21.
 */
public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCV");
    }

    public static native double compareImages(String old_image, String new_image,int type);

}
