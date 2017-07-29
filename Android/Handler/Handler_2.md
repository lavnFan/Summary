<center>
# Handler 系列二
</center>
> 承接上一篇Handler系列一，上篇主要总结了Handler如何通信，这篇来介绍Handler怎么通信。

* Handler的通信机制
* Handler，Looper，MessageQueue如何关联

## Handler 通信机制
![Handler 通信机制](http://7xrhn4.com1.z0.glb.clouddn.com/handler.png)
* 创建Handler，并采用当前线程的Looper创建消息循环系统；
* Handler通过sendMessage(Message)或Post(Runnable)发送消息，调用enqueueMessage把消息插入到消息链表中；
* Looper循环检测消息队列中的消息，若有消息则取出该消息，并调用该消息持有的handler的dispatchMessage方法，回调到创建Handler线程中重写的handleMessage里执行。


## Handler如何关联Looper、MessageQueue
**Handler及其关联的类图**
![Handler及其关联的类图](http://7xrhn4.com1.z0.glb.clouddn.com/handler_Looper_MessageQueue_class.jpg)

以上类图可以快速帮助我们理清Handler与Looper、MessageQueue的关系，以下从源码的角度慢慢分析：

### 1、Handler 发送消息
上一段很熟悉的代码：

     Message msg =Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
     msg.obj = data;
     msg.what=1;   //标志消息的标志
     handler.sendMessage(msg);	

从sendMessageQueue开始追踪，函数调用关系：sendMessage -> sendMessageDelayed ->sendMessageAtTime，在sendMessageAtTime中，携带者传来的message与Handler的mQueue一起通过enqueueMessage进入队列了。

对于postRunnable而言，通过post投递该runnable，调用getPostMessage，通过该runnable构造一个message，再通过 sendMessageDelayed投递，接下来和sendMessage的流程一样了。

#### 2、消息入队列
在enqueueMessage中，通过MessageQueue入队列，并为该message的target赋值为当前的handler对象，记住`msg.target`很重要，之后Looper取出该消息时，还需要由`msg.target.dispatchMessage`回调到该handler中处理消息。

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }

在MessageQueue中,由Message的消息链表进行入队列


    boolean enqueueMessage(Message msg, long when) {
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }

        synchronized (this) {
            if (mQuitting) {
                IllegalStateException e = new IllegalStateException(
                        msg.target + " sending message to a Handler on a dead thread");
                Log.w(TAG, e.getMessage(), e);
                msg.recycle();
                return false;
            }

            msg.markInUse();
            msg.when = when;
            Message p = mMessages;
            boolean needWake;
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
                msg.next = p;
                mMessages = msg;
                needWake = mBlocked;
            } else {
                // Inserted within the middle of the queue.  Usually we don't have to wake
                // up the event queue unless there is a barrier at the head of the queue
                // and the message is the earliest asynchronous message in the queue.
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                Message prev;
                for (;;) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p; // invariant: p == prev.next
                prev.next = msg;
            }

            // We can assume mPtr != 0 because mQuitting is false.
            if (needWake) {
                nativeWake(mPtr);
            }
        }
        return true;
    }




### 3、Looper 处理消息
##### 再说处理消息之前，先看Looper是如何构建与获取的：

* 构造Looper时，构建消息循环队列，并获取当前线程

	    private Looper(boolean quitAllowed) {
        	mQueue = new MessageQueue(quitAllowed);
      		mThread = Thread.currentThread();
    	}
		
* 但该函数是私有的，外界不能直接构造一个Looper，而是通过Looper.prepare来构造的：
	
	  public static void prepare() {
          prepare(true);
      }
      
	  private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper(quitAllowed));
      }	

* 这里创建Looper，并把Looper对象保存在sThreadLocal中，那sThreadLocal是什么呢？
	  
	  static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

	它是一个保存Looper的TheadLocal实例，而ThreadLocal是线程私有的数据存储类，可以来保存线程的Looper对象，这样Handler就可以通过ThreadLocal来保存于获取Looper对象了。

* TheadLocal 如何保存与获取Looper？

      public void set(T value) {
        Thread currentThread = Thread.currentThread();
        Values values = values(currentThread);
        if (values == null) {
            values = initializeValues(currentThread);
        }
        values.put(this, value);
      }

	  public T get() {
        // Optimized for the fast path.
        Thread currentThread = Thread.currentThread();
        Values values = values(currentThread);
        if (values != null) {
            Object[] table = values.table;
            int index = hash & values.mask;
            if (this.reference == table[index]) {
                return (T) table[index + 1];
            }
        } else {
            values = initializeValues(currentThread);
        }

        return (T) values.getAfterMiss(this);
      }

	在 set 中都是通过 `values.put` 保存当前线程的 Looper 实例，通过 `values.getAfterMiss(this)`获取，其中`put`和`getAfterMiss`都有`key`和`value`，都是由Value对象的table数组保存的，那么在table数组里怎么存的呢？

	  table[index] = key.reference;
      table[index + 1] = value;
	
	很显然在数组中，前一个保存着ThreadLocal对象引用的索引，后一个存储传入的Looper实例。

#### 接下来看Looper在loop中如何处理消息
在`loop`中，一个循环，通过`next`取出MessageQueue中的消息

* 若取出的消息为null，则结束循环，返回。
	* 设置消息为空，可以通过MessageQueue的quit和quitSafely方法通知消息队列退出。
* 若取出的消息不为空，则通过msg.target.dispatchMessage回调到handler中去。


    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        final MessageQueue queue = me.mQueue;

        // Make sure the identity of this thread is that of the local process,
        // and keep track of what that identity token actually is.
        Binder.clearCallingIdentity();
        final long ident = Binder.clearCallingIdentity();

        for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }

            // This must be in a local variable, in case a UI event sets the logger
            Printer logging = me.mLogging;
            if (logging != null) {
                logging.println(">>>>> Dispatching to " + msg.target + " " +
                        msg.callback + ": " + msg.what);
            }

            msg.target.dispatchMessage(msg);

            if (logging != null) {
                logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
            }

            // Make sure that during the course of dispatching the
            // identity of the thread wasn't corrupted.
            final long newIdent = Binder.clearCallingIdentity();
            if (ident != newIdent) {
                Log.wtf(TAG, "Thread identity changed from 0x"
                        + Long.toHexString(ident) + " to 0x"
                        + Long.toHexString(newIdent) + " while dispatching to "
                        + msg.target.getClass().getName() + " "
                        + msg.callback + " what=" + msg.what);
            }

            msg.recycleUnchecked();
        }
    }



### 4、handler处理消息
Looper把消息回调到handler的dispatchMessage中进行消息处理：

* 若该消息有callback，即通过Post(Runnable)的方式投递消息，因为在投递`runnable`时，把`runnable`对象赋值给了message的`callback`。
* 若handler的mCallback不为空，则交由通过`callback`创建handler方式去处理。
* 否则，由最常见创建handler对象的方式，在重写handlerMessage中处理。



    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }	

## 总结
以一个时序图来总结handler的消息机制，包含上述如何关联Looper和MessageQueue的过程。   

**Handler-Looper-MessageQueue时序图**
![Handler-Looper-MessageQueue时序图](http://7xrhn4.com1.z0.glb.clouddn.com/handler_Looper_MessageQueue.jpg)


