<center>
# Handler系列
</center>
一、Handler与Thread的通信过程   
二、Handler的原理   
三、Thread与Runnable，线程池，asyncTask

###Handler存在的问题
####内存方面
Handler被作为Activity引用，如果为非静态内部类，则会引用外部类对象。当Activity finish时，Handler可能并未执行完，从而引起Activity的泄漏。故而在所有调用Handler的地方，都用静态内部类。

####异常方面
当Activity finish时,在onDestroy方法中释放了一些资源。此时Handler执行到handlerMessage方法,但相关资源已经被释放,从而引起空指针的异常。   
避免
   
* 如果是使用HandlerMessage，则在方法中加try catch。
* 如果是用post方法，则在Runnable方法中加try catch。

