<center>
# 对图片进行高斯模糊
</center>

## 加载图片
### 调用系统相机
    // create Intent to take a picture and return control to the calling application
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // 指定图片保存的路径
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

    // start the image capture Intent
    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

* 其中若指定了图片保存路径，则在`onActivityResult`中需通过`uri`获取，而从`data`中获取的数据是空的。   
* 若没有指定图片保存路径，那么当手机没有保存时，data返回null，当手机保存在默认路径时，则从`data`中获取。

### 调用预览相机
#### 1、相机预览
可直接从界面拍照或取预览视频中的图片帧，继承`surfaceView`自定义的`CameraPreview`，再作为布局显示。
#### 2、拍照
##### 回调`PictureCallback`
通过`mCamera.takePicture`，设置`pictureCallback`，预览进行拍照停顿，获取图片字节流。再让`camera`重新`starPreview`，保持实时预览。   
 **问题**：预览时进行拍照，屏幕卡顿，可通过第二种方法-回调`PreviewCallback`取图片帧解决。

##### 回调`PreviewCallback`
在预览相机的视频流中，设置`mCamera.setPreviewCallback`获取视频流的数据（格式为`YUV420SP`)转化为字节流，再由`decodeByteArray`获取`bitmap`对象。通过一定时间的的间隔，获取当前图片帧。

## 压缩图片
直接将获取的图片进行高斯模糊，效率太慢，需先将图片压缩。   
图片存在的形式及大小的决定因素  

* 文件形式：`file.length()`
* 流的形式:读入到内存中`byte`字节数
* Bitmap：`bitmap.getByteCount()`   

### 质量压缩
通过压缩图片`options.quality`,但本质上并不会减少图片的像素,只会压缩file文件保存时的大小,但当加载到内存中以bitmap存在时,大小不变。   

	/**
     * 按质量压缩
     *
     * @param image
     * @return
     */
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

### 比例压缩
设置采样率，减少图片的像素，从而对bitmap进行压缩。

    /**
     * 按比例压缩
     *
     * @param image
     * @return
     */
    private Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 4;//be=1表示不缩放
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

## 高斯模糊
#### 周边像素的平均值
每一个像素都取周边像素的平均值，取值范围越大，即模糊半径越大，模糊效果越强烈。   
**问题**：简单平均不合理，因为图像是连续的，越靠近的店关系越密切，越远离的点关系越疏远。  
#### 高斯模糊
周边像素的权值随距离程正态分布，越接近中心，取值越大，越远离中心，取值越小。所以计算平均值时，只需以“中心点”为原点，其他店按其在正太曲线上的位置，分配权重，得到加权平均值。   
**实现**：为加快高斯效率，使用C语言编写，通过JNI调用生成的so库。 


## 提取主色调
**K-means**是一种基于距离的聚类算法，采用距离作为相似的评价指标，把距离靠近的对象组成簇，最终目标就是得到紧凑且独立的簇。  
算法实现思路：选取K个随机的点作为初始聚类中心；再遍历所有点，计算与每个集群中心的距离，并根据最小距离重新对相应对象进行划分；再重新计算每个聚类的均值，一直循环以上计算直到每个聚类的均值不再发生变化为止。

## 对颜色进行梯度变化
###原理
把高斯模糊后的图片提取的主色调作为起始颜色，再取一个终止颜色，使用LinearGradient设置，并通过画布，在视图层中绘制出来。
###实现
1. 根据起始颜色,确定终止颜色
想实现颜色从深到浅的变化，有俩种方案
* 因为提取的颜色是十进制的数，所以想以提取的颜色作为中间值，往上+与往下-进行变化。问题：会出现颜色突变成黄色，可能由于颜色是十进制表示的，所以在不同的RGB颜色上，可以产生突变效果，造成错误的结果。
* 对颜色的三个分量RGB,跟进一定比例进行变化，比如当前颜色的RGB，与白色的RGB，根据效果给定一个比例，产生中间变化的颜色。
 代码如下： 

		    private int getEndColor(int startColor, double percentage) {
		        int sR = Color.red(startColor);
		        int sG = Color.green(startColor);
	        	int sB = Color.blue(startColor);
	       		int wR = Color.red(Color.WHITE);
	        	int wG = Color.green(Color.WHITE);
	        	int wB = Color.blue(Color.WHITE);
	      	    int endColor = (sR + (int) ((sR - wR) * percentage)) / 2 + (sG + (int) ((sG - 		wG) * percentage)) / 2 + (sB + (int) ((sB - wB) * percentage)) / 2;
       	 		return endColor;
  		  }


2. 自定义LinearLayout,并能显示梯度变化的视图
继承LinearLayout的ViewGroup，并实现LiearGradient的绘制，通过setGradient进行颜色的变化。再作为布局使用即可。

		public class MyLinearGradient extends LinearLayout {

  		  private LinearGradient linearGradient;
   		  private Paint paint;
    	  private int width;
     	  private int height;
    	  private int start = 0XFFFF8080;
    	  private int end = 0XFF8080FF;
			
   		  public MyLinearGradient(Context context) {
       		super(context);
        	getDisplay(context);
       	    setWillNotDraw(false);
    	  }
 
   		 public MyLinearGradient(Context context, AttributeSet attrs) {
        	super(context, attrs);
       	    getDisplay(context);
       	    setWillNotDraw(false);
    	 }

    	public MyLinearGradient(Context context, AttributeSet attrs, int defStyleAttr) {
        	super(context, attrs, defStyleAttr);
        	getDisplay(context);
       	    setWillNotDraw(false);
    	 }

    	private void getDisplay(Context context) {
      	   WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
      	   width = wm.getDefaultDisplay().getWidth();
      	   height = wm.getDefaultDisplay().getHeight();
   	 	  }

   		 @Override
  	 	 protected void onDraw(Canvas canvas) {
    		super.onDraw(canvas);
    	    if (paint == null) {
     	       paint = new Paint();
     	   }
     	   if (linearGradient != null) {
			// linearGradient = new LinearGradient(0, 0, width, height, start, end, 	Shader.TileMode.CLAMP);
     	       paint.setShader(linearGradient);
      	      canvas.drawRect(0, 0, width, height, paint);
     	   }
  	 	 }

   		   public void setGradient(int start, int end) {
      		  Log.i("Linear", start + " " + end);
      		  this.start = start;
      		  this.end = end;
      		  linearGradient = new LinearGradient(0, 0, 0, height, start, end, Shader.TileMode.CLAMP);
      		  invalidate();
  	 	 	}
		}


## 图片切换动画
经高斯模糊的图片显示时，设置淡入、淡出动画（ObjectAnimator），可用AnmiatorSet一起播放，使得图片切换顺畅。

## 性能测试
### ADB测试CPU与内存
通过cmd进入adb目录   
内存：adb shell dumpsys meminfo (可加具体包名/pid进行查看)   
cpu：可先通过adb shell进入，在用top -m (number) -t 查看

## 性能优化
### 相机卡顿
问题：在预览时，若回调`takePicture`，则预览需要停顿下来进行拍照，导致卡顿。   
解决：预览拍照改为在预览的视频流里按一定间隔抓取图片，回调`PreviewCallback`。   
### 高斯卡顿
问题：高斯模糊效率比较慢，同步则阻塞主线程，很有可能导致ANR。   
解决：

* 压缩图片大小，
* 异步高斯模糊，避免同步阻塞；每当回调捕捉图片时，新开一个线程进行高斯模糊的耗时操作；且应该使用线程池，防止线程的多次创建与销毁的开销。   

## 使用的线程及线程池
定时拍照：开启一个线程，while循环，每间隔一定时间，投递一个runnable，进行预览拍照。   
高斯模糊：避免线程的多次创建和销毁的开销，使用simpleExecutor，使得处理高斯模糊只有一个核心线程。


## 技术往深入发展 
* handler+thread通信
* 线程与线程池
* 动画
* 图片加载及压缩
* 性能分析