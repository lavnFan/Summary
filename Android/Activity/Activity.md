<center>
# Activity
</center>
Activity 是Android四大组件之一，每个 Activity 都会获得一个用于绘制其用户界面的窗口，用户可与其进行交互。以下来总结 Activity 的重点知识。

## 生命周期
### 正常的生命周期分析
Activity生命周期的回调主要有 ```onCreate()```、```onRestart()```、```onStart()```、```onResume()```、```onPause()```、```onStop()```、```onDestory()``` 几种方法，在生命周期里有**三个嵌套循环**：	


* Activity 的**整个生命周期**发生在 ```onCreate()``` 与 ```onDestroy()``` 之间,在 ```onCreate()``` 中执行“全局”状态设置(例如状态布局)，并在 ```onDestroy()``` 中释放资源。
* Activity 的**可见生命周期**发生在 ```onStart()``` 与 ```onStop()``` 之间，在这段时间内Activity对用户可见。可以在这俩个方法之间保留向用户显示 Activity 所需的资源。在整个生命周期中，当 Activity 在对用户可见和隐藏俩种状态中交替变化时，系统会多次调用 ```onStart()``` 和 ```onStop()```。
* Activity 的**前台生命周期**发生在 ```onResume()``` 与 ```onPause()``` 之间，Activity位于其他 Activity 之前，可与用户交互并具有输入焦点。但状态改变频繁，建议做些轻量级操作。

<center>
![生命周期][1]
</center>

* ```onCreate()```：**首次创建 Activity 时**调用。在这个方法里执行所有正常的静态设置-创建视图、将数据绑定到列表等等。
* ```onRestart()```：**已停止并即将再次启动前**调用。
* ```onStart()```：**在 Activity 即将对用户可见之前**调用。如果 Activity 转入前台，则后接 ```onResume()``` ，如果 Activity 转入隐藏状态，则后接 ```onStop()``` 。
* ```onResume()```：**在 Activity 即将开始与用户进行交互之前调用**。此时，Activity 处于任务栈的顶层，并具有用户输入焦点。
* ```onPause()```：**当系统即将开始继续另一个 Activity 时调用**。此方法通常用于确认对持久性数据的未保存更改，停止动画以及其他可能消耗CPU的内容。它应该迅速地执行所需操作，因为它返回后，下一个 Activity 才能继续执行。如果 Activity 返回前台，则后接 ```onResume()``` ，如果 Activity 转入对用户不可见状态，则后接 ```onStop()``` 。
* ```onStop()```：**在 Activity 对用户不再可见时**调用。如果 Activity 恢复与用户的交互，则后接 ```onRestart()``` ，如果 Activity 被销毁，则后接 ```onDestroy()``` 。
* ```onDestroy()```：**在 Activity 被销毁前**调用。当 Activity 因为被调用 ```finish()``` 或系统为节省空间而暂时销毁该 Activity 实例时，可能会调用它，可以通过 ```isFinishing()``` 方法来区分。

#### 图中周期说明
* 正常启动一个 Activity ，回调如下：```onCreate()->onStart()->onResume()``` 。
* 当用户打开新的 Activity 或切换到桌面时，回调如下：```onPause()->onStop()```，有一种特殊情况:如果新的 Activity 采用了透明主题，那么当前 Activity 不会回调 ```onStop()```。
* 当用户再次回到原 Activity 时，回调如下：```onRestart()->onStart()->onResume()```。
* 当用户按back键回退时，回调如下：```onPause()->onStop()->onDestroy()```。
* 从整个生命周期来看，```onCreate()``` 与 ```onDestroy()``` 配对，只会调用一次。从 Activity 的可见状态来说，```onStart()``` 与 ```onStop()``` 配对，会调用多次。从 Activity 是否在前台可交互来说， ```onResume()``` 与 ```onPause()``` 配对，可调用多次。

#### 周期相关问题
##### ```onStart()``` 和 ```onResume()``` ，```onPause()``` 和 ```onStop()``` 的区别？
* ```onStart()``` 和 ```onStop()``` 是从 Activity 是否可见来回调的，```onResume()``` 和 ```onPause()``` 是从 Activity 是否位于前台来回调的。

##### 俩个 Activity A 和 B，那么 A 启动 B 的过程中，A 的 ```onPause()``` 与 B 的 ```onResume()``` 哪个先执行？
* 新 Activity 启动之前，栈顶的 Activity 需要先 ```onPause()``` 后，新的 Activity 才能启动的。

### 异常的生命周期分析
当 Activity 暂停或停止时，Activity 对象仍保留在内存中-有关其成员和当前状态仍处于活动状态。因此，用户在 Activity 内所做的任何更改都会得到保留，这样一来，当 Activity 返回前台时，这些更改仍然存在。   
但是当资源相关的系统配置发生改变或系统内存不足时，Activity 对象可能会被销毁，这时继续 Activity 时根本无法让其状态保持完好，而是必须在用户返回 Activity 时创建  Activity 对象，这些状态信息会由 ```onSaveInstanceState()``` 来保存。

<center>
![数据恢复][2]
</center>

* 当 Activity 被异常终止后，系统会调用 ```onSaveInstanceState()``` 来保存当 Activity 的状态。这个方法的调用时机是在 ```onStop()``` 之前，和 ```onPause()``` 没有既定的时序关系。当 Activity 被重新创建后，系统会调用 ```onRestoreInstanceState()``` ，并且把 Activity 销毁时通过 ```onSaveInstanceState()``` 保存的 ```Bundle``` 对象同时传递给 ```onRestoreInstanceState()``` 和 ```onCreate()``` ，从时序上来说， ```onRestoreInstanceState()``` 在 ```onStart()``` 之后。
* 无法保证系统会在销毁 Activity 前调用 ```onSaveInstanceState()``` ，因为当正常离开当前页面，显示关闭 Activity 时，不需要保存状态。而且在异常状态下无法确定调用 ```onSaveInstanceState()``` 的时机，所以只用它来保存 Activity 的瞬间状态，不要来保存持久性数据。 
* Activity 类的 ```onSaveInstanceState()``` 默认实现会恢复部分 Activity 状态，具体地来说，默认实现会为布局中的每个 View 调用相应的 ```onSaveInstanceState()``` 方法，让每个视图都能提供有关自身的应保存信息。 Android 框架中几乎每个小部件都会根据需要实现此方法，以便在重建 Activity 时自动保存和恢复对UI所做的任何可见更改。我们只需要为保存其状态的每个小部件提供一个唯一的ID，如果小部件没有ID，则系统无法保存其状态。
* 系统配置发生改变后，Activity 会被重新创建，但我们可以指定 configChanges 属性，不重新创建。比如不想让屏幕旋转时重新创建，给 configChanges 属性添加 orientation 值，如下，```android:configChanges="orientation"```。

## 任务和返回栈
* 任务：在执行特定作业时与用户交互的一系列 Activity。
* 返回栈：这些 Activity 按照各自的打开顺序排列在堆栈中。

当前 Activity 启动另一个 Activity 时，该新的 Activity 会被推送到堆栈顶部，成为焦点所在。前一个 Activity 仍保留在堆栈中，但是处于停止状态。当用户按“返回”键时，当前 Activity 会从堆栈顶部弹出(被销毁)，而前一个 Activity 恢复执行。堆栈中的 Activity 永远不会重新排列，仅推入和弹出堆栈，并按照“后进先出”对象结构运行。

### 启动模式
定义 Activity 的新实例如何与当前任务关联,来改变系统的默认行为。

#### standard
**默认模式**。系统在每次启动 一个Activity 都会重新创建一个新实例，不管这个实例是否已经存在。非 Activity 类型的 Context 没有任务栈，用 getApplicationContext 直接启动 Activity 会报错，可以加一个标志位 ```FLAG_ACTIVITY_NEW_TASK``` ，实际以 singleTask 模式启动的一个新的任务栈。

#### singleTop
**栈顶复用模式**。如果新的 Activity 已经位于任务栈的栈顶，那么此 Activity 不会被重新创建，同时通过 ```onNewIntent()``` 方法传递 Intent。如果新 Activity 的实例已经存在但不位于栈顶，那么新的 Activity 仍然会被重新创建。

#### singleTask
**栈内复用模式**。是一种单实例模式，在这种模式下，如果该 Activity 的一个实例已经存在于一个单独的任务中，系统会通过调用现有实例的 ```onNewIntent()``` 方法向其传送 Intent，而不是创建新实例。如果启动的 Activity 位于当前任务栈中，启动该 Activity 时，会把该 Activity 切换到栈顶后，**还将导致它上边的 Activity 全部出栈**。如果启动的 Activity 位于后台任务栈中，则整个后台任务栈会被切换到前台。

#### singleInstance
**单实例模式**。是一种加强的 singleTask 模式。和 singleTask 相同，只是具有此种模式的 Activity 只能单独位于一个任务栈中。

### 使用
#### 清单文件
在清单文件中声明 Activity 时，使用 ```<activity>``` 元素的 ```lauchMode``` 属性指定 Activity 应该如何与任务关联。

#### Intent 标志
启动 Activity 时，通过 ```startActivity()``` 的 Intent 中加入相应的标志，修改 Activity 与其任务的默认关联方式。

* FLAG_ACTIVITY_NEW_TASK：产生与 “singleTask” 模式相同的行为。
* FLAG_ACTIVITY_SINGLE_TOP：产生 “singleTop” 模式相同的行为。
* FLAG_ACTIVITY_CLEAR_TOP：如果正在启动的 Activity 已在当前任务中运行，则会销毁倩倩任务顶部的所有 Activity ，并通过 ```onNewIntent()``` 将此 Intent 传递给 Activity 已恢复的实例（现在位于顶部），而不是启动该 Activity 的新实例。通常与 FLAG_ACTIVITY_NEW_TASK 结合使用，就会等同于“singleTask” 模式。
* FLAG_ACTIVITY_EXCLUDE_RECENTS：具有这个标记的 Activity 不会出现在任务栈中，可用于不会通过历史列表回到这个页面的情况，效果等同于 ```android:exludeFromRecents="true"``` 。

## Intent 匹配规则
Intent 是一个消息传递对象，可以向其他应用组件发送请求操作。

### Intent 类型
* 显示 Intent：按名称（完全限定类名）指定要启动的组件。需要明确知道启动对象的组件信息，包括包名和类名。
* 隐式 Intent：不会指定特定的组件，而是声明要执行的常规操作，从而允许其他应用中的组件来处理它。创建隐式 Intent 时，Android 系统通过将 Intent 的内容与设备上其他应用的清单文件中声明的 intent-filter 进行比较，若 Intent 与 intent-filter 匹配，则系统内启动该组件，若有多个匹配，则通过显示的对话框来支持用户选择要使用的应用。


当一个 Intent 同时匹配 activity 声明的任何一组 action，category 和 data 时，就可成功启动对应的应用。

### action
指定要执行的通用操作的字符串。Intent 中必须有一个 action ，且必须能够和过滤规则中的某个 action 相同即可匹配成功。

### category
处理 Intent 组件类型的附加信息的字符串。如果 Intent 中含有 category，那么**所有的 category** 都必须和过滤规则中的其中一个 category 相同。换句话说，Intent 里的category 都必须是过滤规则中的 category。Intent 可以没有 category，因为系统会默认加上 ```android.intent.category.DEFAULT```，这时对于接收的 activity ，就必须在 intent-filter 里加上这个默认的类别了。

### data
引用待操作数据和/或该数据 MIME 类型的 URI ( **Uri** 对象)。data 由俩部分组成， mimeType 和 URI 。

* mimeType：指媒体类型，比如 image/jpg、audio/mpeg4-generic 和 video/* 等。
* URI ：包含的数据比较多，具体结构： ```<scheme>://<host>:<port>[<path>|<pathPrefix>|<pathPattern>]``` ，Scheme 表示 URI 的模式，如 http，file，content 等。

可以用 ```setData()``` 设置 URI，```setType()``` 设置 MIME 类型，但同时使用时，请使用 ```setDataAndType()``` ，因为 调用 ```setData()``` 和 ```setType()``` 会互相抵消彼此的值。匹配规则和 action 类似，它要求 Intent 中必须含有 data 数据，且 data 数据能够完全匹配过滤规则中的某个 data 。

### Extra
携带完成请求操作所需的附加信息的键值对。

### Flag
Intent 的标志，来指示 Android 系统如何启动 Activity 以及启动之后如何处理。

[1]:http://7xrhn4.com1.z0.glb.clouddn.com/activity_lifecycle.png 
[2]:http://7xrhn4.com1.z0.glb.clouddn.com/restore_instance.png