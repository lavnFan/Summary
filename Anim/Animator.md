<center>
# Android 动画
</center>

* 动画有哪些？
* 动画如何实现？
* 动画需要注意的地方？

## 动画框架
**[View Animation](###)**   
* **[Frame](#Frame动画)**
* **[Tween](#Tween动画)**

**[Property Animation](##Property动画)**

## Frame动画
顺序播放一组预先定义好的图片，有动画播放效果。   
文件目录：res/drawable/filename.xml    
编译资源数据类型：AnimationDrawable    
资源引用：

* Java: R.drawable.filename
* XML: @[package:]drawable.filename

```
	<?xml version="1.0" encoding="utf-8"?>
	<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
   	android:oneshot=["true" | "false"] >
	<item
		android:drawable="@[package:]drawable/drawable_resource_name"
		android:duration="integer" />
	</animation-list>
```

### XML定义动画
* 定义一个animation-list，设置一组动画图片及持续时间
* 创建AnimationDrwable，读取动画
* 为View设置该动画

```
	<?xml version="1.0" encoding="utf-8"?>
	<animation-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/progress_1" android:duration="200"/>
    <item android:drawable="@drawable/progress_2" android:duration="200"/>
    <item android:drawable="@drawable/progress_3" android:duration="200"/>
    <item android:drawable="@drawable/progress_4" android:duration="200"/>
    <item android:drawable="@drawable/progress_5" android:duration="200"/>
    <item android:drawable="@drawable/progress_6" android:duration="200"/>
    <item android:drawable="@drawable/progress_7" android:duration="200"/>
    <item android:drawable="@drawable/progress_8" android:duration="60"/>
	</animation-list>
```

`FrameActivity.java`中

	 /**
     *  从XML中读取animation-list
     */
    private void setFrameDrawableAnimation() {
        frameDrawableAnim = (AnimationDrawable) getResources()
                .getDrawable(R.drawable.dynamic_road_running);
        mFrameIv.setImageDrawable(frameDrawableAnim);
        frameDrawableAnim.start();
    }


### Java代码实现动画
    /**
     * 代码控制，依次添加帧动画的每一帧图片
     */
    private void setFrameCodeAnimation() {
        frameCodeAnim = new AnimationDrawable();
        frameCodeAnim.setOneShot(false);  //true：动画只显示一次，这里设置为false，持续显示动画
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_1),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_2),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_3),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_4),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_5),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_6),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_7),200);
        frameCodeAnim.addFrame(getResources().getDrawable(R.drawable.progress_8),60);
        mFrameCodeIv.setImageDrawable(frameCodeAnim);
        frameCodeAnim.start();
    }

### 实现效果

<img src="http://7xrhn4.com1.z0.glb.clouddn.com/Frame.gif" alt="帧动画" align=center width="320" height="480" />


## Tween动画

![动画类图](http://7xrhn4.com1.z0.glb.clouddn.com/Animation.png)

文件位置: res/anim/filename.xml   
编译资源的数据类型：Animation。    
资源引用：
   
* Java: R.anim.filename    
* XML: @[package:]anim/filename    

```
	<?xml version="1.0" encoding="utf-8"?>
	<set xmlns:android="http://schemas.android.com/apk/res/android"
	    android:interpolator="@[package:]anim/interpolator_resource"
	    android:shareInterpolator=["true" | "false"] >
	    <alpha
		android:fromAlpha="float"
		android:toAlpha="float" />
	    <scale
		android:fromXScale="float"
		android:toXScale="float"
		android:fromYScale="float"
		android:toYScale="float"
		android:pivotX="float"
		android:pivotY="float" />
	    <translate
		android:fromXDelta="float"
		android:toXDelta="float"
		android:fromYDelta="float"
		android:toYDelta="float" />
	    <rotate
		android:fromDegrees="float"
		android:toDegrees="float"
		android:pivotX="float"
		android:pivotY="float" />
	    <set>
		...
	    </set>
	</set>
```

### 动画种类
#### TranslateAnimation
`<translate>`：平移动画

* android:fromXDelta 
* android:toXDelta
* android:fromYDelta
* android:toYDelta

属性值代表起始/结束方向的位置，可有俩种表示：

* Float:是一个绝对值，表示相对于自身原始位置的像素值
* 百分值:浮点数num%、num%p
	* 以num%表示，代表相对于自己的百分比，比如toXDelta定义为100%就表示在X方向上移动自己的1倍距离
	* 以num%p表示，代表相对于父类组件的百分比

#### ScaleAnimation
`<scale>`：缩放动画，动态调控件尺寸的效果

* android:fromXScale
* android:toXScale
* android:fromYScale
* android:toYScale

Float值表示，为动画起始到结束时，X、Y坐标上的伸缩尺寸

* 0.0表示收缩到没有
* 1.0表示正常无伸缩

通过设置pivotX和pivotY可以指定image缩放的中心点。如果我们想表示中轴点为图像的中心，我们可以把两个属性值定义成0.5或者50%

* android:pivotX
* android:pivotY


#### RotateAnimation
`<rotate>`：旋转动画

* android:fromDegrees
* android:toDegrees

代表起始和结束的角度，浮点值，单位：度。

* android:pivotX 
* android:pivotY 

Float值或者百分比表示：浮点数、num%、num%p。

* 数字方式代表相对于自身左边缘的像素值，
* num%方式代表相对于自身左边缘或顶边缘的百分比
* num%p方式代表相对于父容器的左边缘或顶边缘的百分比。


#### AlphaAnimation
`<alpha>`：透明度动画

* android:fromAlpha
* android:toAlpha

Float值表示，代表动画开始和结束时透明度，0.0表示完全透明，1.0表示完全不透明。

### 代码实现
* 在drawable/anim目录下，创建xml文件
* 定义set集合，及四种动画效果
* 在代码中读取该动画，再为某个view设置

```
	<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:fillAfter="false"    <--!动画结束后View是否停留在结束位置-->
    android:zAdjustment="normal">

    <translate
        android:duration="1000"
        android:fromXDelta="100"
        android:fromYDelta="0"
        android:interpolator="@android:anim/linear_interpolator"
        android:toXDelta="100"
        android:toYDelta="100" />

    <rotate
        android:duration="2000"
        android:fromDegrees="0"
        android:toDegrees="40" />

    <scale
        android:duration="2000"
        android:fromXScale="1.0"
        android:fromYScale="1.0"
        android:pivotX="0.5"
        android:pivotY="50%"
        android:toXScale="2.0"
        android:toYScale="2.0" />

    <alpha
        android:duration="2000"
        android:fromAlpha="0.3"
        android:toAlpha="1.0" />

	</set>
```

代码中为某个view设置该动画，并开始播放

	tweenAnimation = new AnimationUtils().loadAnimation(this, R.anim.tween_animation);
    tweenIv.startAnimation(tweenAnimation);

### 实现效果

<img src="http://7xrhn4.com1.z0.glb.clouddn.com/TweenAnimation.gif" alt="补间动画" align=center />

## Property动画

![属性动画类图](http://7xrhn4.com1.z0.glb.clouddn.com/Animator.png)

可实现效果：在一个时间间隔内完成对象从一个属性值到另一个属性值的改变。   
目录：res/animator/filename.xm   
编译后的资源为：ValueAnimator, ObjectAnimator, or AnimatorSet。　   
XML文件的根元素必须为set,可以嵌套。

```
	<set
  		android:ordering=["together" | "sequentially"]>

    <objectAnimator
        android:propertyName="string"
        android:duration="int"
        android:valueFrom="float | int | color"
        android:valueTo="float | int | color"
        android:startOffset="int"
        android:repeatCount="int"
        android:repeatMode=["repeat" | "reverse"]
        android:valueType=["intType" | "floatType"]/>

    <animator
        android:duration="int"
        android:valueFrom="float | int | color"
        android:valueTo="float | int | color"
        android:startOffset="int"
        android:repeatCount="int"
        android:repeatMode=["repeat" | "reverse"]
        android:valueType=["intType" | "floatType"]/>

    <set>
        ...
    </set>
	</set>
```

### 属性说明
* ordering：设置子动画启动方式是先后有序的还是同时
* android:propertyName：String类型，必须要设定的值，代表要执行动画的属性，通过名字引用，比如你可以指定了一个View的”alpha” 或者 “backgroundColor” ，这个objectAnimator元素没有暴露target属性，因此比不能够在XML中执行一个动画，必须通过调用loadAnimator() 填充你的XML动画资源，并且调用setTarget() 应用到拥有这个属性的目标对象上。
* android:valueFrom：相对应valueTo，动画的起始点，如果没有指定，系统会通过属性身上的get 方法获取 ，颜色也是6位十六进制的数字表示。
* android:valueTo：Float、int或者color，也是必须值，表明了动画结束的点，颜色由6位十六进制的数字表示。
* android:duration：动画的时长，int类型，以毫秒为单位，默认为300毫秒。
* android:startOffset：动画延迟的时间，从调用start方法后开始计算，int型，毫秒为单位
* android:repeatCount：一个动画的重复次数，int型，”-1“表示无限循环，”1“表示动画在第一次执行完成后重复执行一次，也就是两次，默认为0，不重复执行。
* android:repeatMode：重复模式：int型，当一个动画执行完的时候应该如何处理。该值必须是正数或者是-1，“reverse”会使得按照动画向相反的方向执行，可实现类似钟摆效果。“repeat”会使得动画每次都从头开始循环。
* android:valueType：关键参数，如果该value是一个颜色，那么就不需要指定，因为动画框架会自动的处理颜色值。有intType和floatType两种：分别说明动画值为int和float型。

### 实例说明
**场景**：俩张图片切换，中间加上淡入、淡出的动画效果。   
**分析**：多种动画，可以用AnimatorSet。

代码实现

	 private void setObjectAnimation() {
        setObjectAnimation();
        final ImageView newBgView = mCurView == mAnimationView01Iv ? mAnimationView02Iv : mAnimationView01Iv;
        changeDrawable();      //俩张图片轮询更替
        newBgView.setImageDrawable(drawable);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(mCurView, "alpha", 1.0f, 0.0f);     //前一张图片淡出
        ObjectAnimator appearAnim = ObjectAnimator.ofFloat(newBgView, "alpha", 0.0f, 1.0f);   //后一张图片淡入
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(2000);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(fadeAnim, appearAnim);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurView = newBgView;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void changeDrawable() {
        if (bDrawable_1) {
            drawable = getResources().getDrawable(R.drawable.cat2);
            bDrawable_1 = false;
        } else {
            drawable = getResources().getDrawable(R.drawable.cat);
            bDrawable_1 = true;
        }
    }

### 效果展示
<img src="http://7xrhn4.com1.z0.glb.clouddn.com/ObjectAnimation.gif" alt="属性动画" align=center />

## View Animation 与 Property Animation 的比较
### View Animation
* 只能为View添加动画
* 支持动画比较少，如不支持背景颜色变化的动画
* 动画变化时，改变的是View的绘制效果，真正的View属性保持不变，如无论如何缩放或平移View，它的有效点击区域并没有扩展到其他动画效果区域，它的位置和大小总是不变的。

### Property Animation:
* 可以为任何一个对象添加动画，同事对象自身属性也会修改。
* 属性动画处理动画更强大，可指定动画的多个属性， 并可多个动画一起展示。

## 使用动画的注意事项
### OOM
当图片比较大时，容易出现OOM，因此多注意帧动画所呈现的一组动画的内存。

### 内存泄漏
属性动画中若设置的动画是无限循环的动画，则这类动画再Activity退出时要及时停止。

### 兼容性问题
属性动画是在 API 11 才加入的，所有在系统比较低的版本中可以使用兼容性的动画Nineoldandroids。
