<center> 
# Markdown语法 #
</center>

# 标题，分六个等级(一级) #
## 二级
### 三级
#### 四级
##### 五级
###### 六级

## 文本 ##
*斜体*  
**粗体**  
***粗斜体***  
换行，结尾加2空格

若新起一个段落，则空一行或用`---`分隔符

#### 字体与颜色 ####
<font face="微软雅黑" size=4 color=gray>
微软雅黑，字体大小为1~7,颜色为黑色(或用RGB表示)
</font>

## 链接 ##
#### 文字链接 ####
[直接google](https://www.google.com.hk/webhp?hl=zh-CN&sourceid=cnhp&gws_rd=ssl)
[间接google][1]  
[1]:https://www.google.com.hk/webhp?hl=zh-CN&sourceid=cnhp&gws_rd=ssl   

#### 新窗口链接
<a href="https://github.com/lavnFan/Summary/tree/master/Handler%E9%80%9A%E4%BF%A1/Handler" target="_blank">github</a> 

#### 图片 ####
![图片](http://www.2345.com/girl/ztmm/liuyifei/3.jpg)
<img src="http://img1.shenchuang.com/2015/0805/1438767760805.png" width="260" height="313">

#### 锚点 #####
* 先定义锚点id：<a href="#end">Goto the End!</a>或[Goto the End!](#end)
* 再定义一个id为end的对象


## 列表 ##
####普通无序列表####
- 减号
+ 加号
* 号

#### 普通有序列表 ####
1. 列表前使用
7. 第二行，自动纠正

#### 列表嵌套 #####
1. 列出所有元素
	- 无序列元素 A
		1. 元素 A的有序子列表

#### 表格 ####
| Tables | Are | Cool |
|:-------|:------:|-----:|
|左边对齐    | right-aligned | 右边对齐|
|zebra stripes | baz  |   baz|

## 引用 ##
普通引用
> 引用文本前使用  
> 哈哈
>> 嵌套多层  
>    ``` 代码块 ```   


## 代码 ##
使用`function`,也可使用```function```

	或使用四空格缩进，再贴代码
	public void main(){
		System.out.println("Hello World!");    
	}
	



<p id="end">The End!</p>