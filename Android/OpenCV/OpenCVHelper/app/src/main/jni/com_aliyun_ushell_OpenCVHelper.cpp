//
// Created by wf117833 on 2016/7/21.
//
#include "com_aliyun_ushell_OpenCVHelper.h"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>
#include <cv.h>
#include <highgui.h>

using namespace cv;
using namespace std;

extern "C" {

JNIEXPORT jdouble JNICALL Java_com_aliyun_ushell_OpenCVHelper_compareImages
  (JNIEnv *, jclass, jstring, jstring, jint);

JNIEXPORT jdouble JNICALL Java_com_aliyun_ushell_OpenCVHelper_compareImages
  (JNIEnv *env, jclass obj, jstring old_image, jstring new_image, jint type)
  {
 	const char* old_str =env->GetStringUTFChars(old_image,0);
 	const char* new_str = env->GetStringUTFChars(new_image,0);

 	Mat src_blur_old,src_old,hsv_old;
 	Mat src_blur_new,src_new,hsv_new;

 	src_blur_old = imread(old_str,1);
 	src_blur_new = imread(new_str,1);

    //对俩张图片进行高斯模糊
    GaussianBlur( src_blur_old, src_old, Size( 49, 49 ), 0, 0); //src,dst,size
    GaussianBlur( src_blur_new, src_new, Size( 49,49 ), 0, 0);

    //保存俩张图片到固定路径
//    imwrite("/sdcard/Boohee/gauss_old.jpg",src_old);
//    imwrite("/sdcard/Boohee/gauss_new.jpg",src_new);

     //比较俩张图片的直方图

   // 转换到 HSV
 	cvtColor(src_old,hsv_old,CV_BGR2HSV );
 	cvtColor(src_new,hsv_new,CV_BGR2HSV );

 	// 对hue通道使用30个bin,对saturatoin通道使用32个bin
 	int h_bins = 50; int s_bins = 60;
 	int histSize[] = { h_bins, s_bins };

 	// hue的取值范围从0到256, saturation取值范围从0到180
	float h_ranges[] = { 0, 256 };
 	float s_ranges[] = { 0, 180 };

 	const float* ranges[] = { h_ranges, s_ranges };

 	// 使用第0和第1通道
 	int channels[] = { 0, 1 };

	// 直方图
 	MatND hist_old;
 	MatND hist_new;

	// 计算HSV图像的直方图
   calcHist( &hsv_old, 1, channels, Mat(), hist_old, 2, histSize, ranges, true, false );
   normalize( hist_old, hist_old, 0, 1, NORM_MINMAX, -1, Mat() );

	calcHist( &hsv_new, 1, channels, Mat(), hist_new, 2, histSize, ranges, true, false );
 	normalize( hist_new, hist_new, 0, 1, NORM_MINMAX, -1, Mat() );

  //应用不同的直方图对比方法
    double old_new;
    int compare_method = type;
    old_new = compareHist( hist_old, hist_new, compare_method );

 	env->ReleaseStringUTFChars(old_image,old_str);
 	env->ReleaseStringUTFChars(new_image,new_str);

 	return old_new;

    }
}