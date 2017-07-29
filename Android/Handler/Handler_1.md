<center>
# Handler 系列一
</center>

* Handler 是什么？
* 为什么需要 Handler？
* Handler 如何使用？
* handler 存在的问题及改进？

**Handler**：是一个消息分发对象，进行发送和处理消息，并且其 Runnable 对象与一个线程的 MessageQueue 关联。     
**作用**：调度消息，将一个任务切换到某个指定的线程中去执行。

## 为什么需要 Handler？
### 子线程不允许访问 UI
假若子线程允许访问 UI，则在多线程并发访问情况下，会使得 UI 控件处于不可预期的状态。   
传统解决办法：加锁，但会使得UI访问逻辑变的复杂，其次降低 UI 访问的效率。

### 引入 Handler
采用单线程模型处理 UI 操作，通过 Handler 切换到 UI 线程，解决子线程中无法访问 UI 的问题。 

## Handler 使用
### 方式一： post(Runnable)
* 创建一个工作线程，实现 Runnable 接口，实现 run 方法，处理耗时操作
* 创建一个 handler，通过 handler.post/postDelay，投递创建的 Runnable，在 run 方法中进行更新 UI 操作。

		new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 耗时操作
                 */
               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       /**
                        * 更新UI
                        */
                   }
               });
            }
        }).start();

### 方式二： sendMessage(Message)
* 创建一个工作线程，继承 Thread，重新 run 方法，处理耗时操作
* 创建一个 Message 对象，设置 what 标志及数据
* 通过 sendMessage 进行投递消息
* 创建一个handler，重写 handleMessage 方法，根据 msg.what 信息判断，接收对应的信息，再在这里更新 UI。
		   
	    private Handler handler = new Handler(){
      	  @Override
      	  public void handleMessage(Message msg) {
          	  super.handleMessage(msg);
          	  switch (msg.what) {      //判断标志位
           	     case 1:
           	         /**
           	          * 获取数据，更新UI
          	           */
          	          break;
         	   }
        	 }
     	  };
		

		public class WorkThread extends Thread {
 	
          @Override
   	      public void run() {
   	        super.run();
     	   /**
    	     * 耗时操作
     	    */
       
           Message msg =Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
           msg.obj = data;
           msg.what=1;   //标志消息的标志
           handler.sendMessage(msg);
        }
		
		new WorkThread().start();


## Handler 存在的问题
###内存方面
Handler 被作为 Activity 引用，如果为非静态内部类，则会引用外部类对象。当 Activity finish 时，Handler可能并未执行完，从而引起 Activity 的**内存泄漏**。故而在所有调用 Handler 的地方，都用静态内部类。

### 异常方面
当 Activity finish 时,在 onDestroy 方法中释放了一些资源。此时 Handler 执行到 handlerMessage 方法,但相关资源已经被释放,从而引起**空指针的异常**。   
**避免**
   
* 如果是使用 handlerMessage，则在方法中加try catch。
* 如果是用 post 方法，则在Runnable方法中加try catch。

## Handler 的改进
* 内存方面：使用静态内部类创建 handler 对象，且对 Activity 持有弱引用
* 异常方面：不加 try catch，而是在 onDestory 中把消息队列 MessageQueue 中的消息给 remove 掉。

则使用如下方式创建 handler 对象：
			
        /**
        * 为避免handler造成的内存泄漏
        * 1、使用静态的handler，对外部类不保持对象的引用
        * 2、但Handler需要与Activity通信，所以需要增加一个对Activity的弱引用
        */
      	private static class MyHandler extends Handler {
        	private final WeakReference<Activity> mActivityReference;	

	        MyHandler(Activity activity) {
	            this.mActivityReference = new WeakReference<Activity>(activity);
	        }

	        @Override
		        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	            MainActivity activity = (MainActivity) mActivityReference.get();  //获取弱引用队列中的activity
	            switch (msg.what) {    //获取消息，更新UI
	                case 1:
	                    byte[] data = (byte[]) msg.obj;
	                    activity.threadIv.setImageBitmap(activity.getBitmap(data));
	                    break;
	            }
	        }
	    }

并在 onDesotry 中销毁：

	@Override
    protected void onDestroy() {
        super.onDestroy();
        //避免activity销毁时，messageQueue中的消息未处理完；故此时应把对应的message给清除出队列
        handler.removeCallbacks(postRunnable);   //清除runnable对应的message
        //handler.removeMessage(what)  清除what对应的message
    }

## Handler 的使用实现
* 耗时操作采用从网络加载一张图片
* 继承 Thread 或实现 Runnable 接口的线程，与 UI 线程进行分离，其中 Runnable 与主线程通过回调接口进行通信，降低耦合，提高代码复用性。

### 代码，同步在 <a href="https://github.com/lavnFan/Summary/tree/master/Handler%E9%80%9A%E4%BF%A1/Handler" target="_blank">github</a> 上

####在 Activity 中创建 handler 对象，调用工作线程执行
		
	public class MainActivity extends AppCompatActivity {

    ImageView threadIv;
    ImageView runnableIv;
    SendThread sendThread;
    PostRunnable postRunnable;
    private final MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        threadIv = (ImageView) findViewById(R.id.thread_iv);
        runnableIv = (ImageView) findViewById(R.id.runnable_iv);

        sendThread = new SendThread(handler);
        sendThread.start();

        postRunnable = new PostRunnable(handler);
        postRunnable.setRefreshUI(new PostRunnable.RefreshUI() {
            @Override
            public void setImage(byte[] data) {
                runnableIv.setImageBitmap(getBitmap(data));
            }
        });
        new Thread(postRunnable).start();


    }

    /**
     * 为避免handler造成的内存泄漏
     * 1、使用静态的handler，对外部类不保持对象的引用
     * 2、但Handler需要与Activity通信，所以需要增加一个对Activity的弱引用
     */
    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivityReference;

        MyHandler(Activity activity) {
            this.mActivityReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = (MainActivity) mActivityReference.get();  //获取弱引用队列中的activity
            switch (msg.what) {    //获取消息，更新UI
                case 1:
                    byte[] data = (byte[]) msg.obj;
                    activity.threadIv.setImageBitmap(activity.getBitmap(data));
                    break;
            }
        }
    }

    private Bitmap getBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //避免activity销毁时，messageQueue中的消息未处理完；故此时应把对应的message给清除出队列
        handler.removeCallbacks(postRunnable);   //清除runnable对应的message
        //handler.removeMessage(what)  清除what对应的message
    }
	}


####方式一：实现 runnable 接口，通过 post（Runnable）通信，并通过给定的回调接口通知 Activity 更新

	public class PostRunnable implements Runnable {

    private Handler handler;
    private RefreshUI refreshUI;
    byte[] data = null;

    public PostRunnable(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        /**
         * 耗时操作
         */
        final Bitmap bitmap = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://i3.17173cdn.com/2fhnvk/YWxqaGBf/cms3/FNsPLfbkmwgBgpl.jpg");
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                data = EntityUtils.toByteArray(httpResponse.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回结果给UI线程
        handler.post(new Runnable() {
            @Override
            public void run() {
                refreshUI.setImage(data);
            }
        });
    }

    public interface RefreshUI {
        public void setImage(byte[] data);
    }

    public void setRefreshUI(RefreshUI refreshUI) {
        this.refreshUI = refreshUI;
    }
	}
	
####方式二:继承Thread，通过handler的sendMessage通信

	public class SendThread extends Thread {

    private Handler handler;

    public SendThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        /**
         * 耗时操作
         */
        byte[]data=null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("https://d36lyudx79hk0a.cloudfront.net/p0/descr/pc27/3095587d8c4560d8.png");
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode()==200){
                data= EntityUtils.toByteArray(httpResponse.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回结果给UI线程
        doTask(data);
    }

    /**
     * 通过handler返回消息
     * @param data
     */
    private void doTask(byte[] data) {
        Message msg =Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
        msg.obj = data;
        msg.what=1;   //标志消息的标志
        handler.sendMessage(msg);
    }
	}

