<center>
# 排序常用算法总结
</center>

**排序算法：**一种能将一串数据依照特定的排序方式进行排列的一种算法。  
**排序算法性能：**取决于时间和空间复杂度，其次还得考虑稳定性，及其适应的场景。  
**稳定性：**让原本有相等键值的记录维持相对次序。也就是若一个排序算法是稳定的，当有俩个相等键值的记录R和S，且原本的序列中R在S前，那么排序后的列表中R应该也在S之前。  

以下来总结常用的排序算法，加深对排序的理解。
## 排序算法目录

* [冒泡排序](#冒泡排序)
* [插入排序](#插入排序)
* [希尔排序](#希尔排序)
* [选择排序](#选择排序)
* [快速排序](#快速排序)
* [归并排序](#归并排序)
* [堆排序](#堆排序)
* [计数排序](#计数排序)
* [桶排序](#桶排序)
* [基数排序](#基数排序)
* [总结](#总结)

## <p id="冒泡排序">冒泡排序</p> ##

### 原理 ###

俩俩比较相邻记录的排序码，若发生逆序，则交换；有俩种方式进行冒泡，一种是先把小的冒泡到前边去，另一
种是把大的元素冒泡到后边。
### 性能 ###
时间复杂度为O(N^2)，空间复杂度为O(1)。排序是稳定的，排序比较次数与初始序列无关，但交换次数与初始序列有关。	
### 优化 ###
若初始序列就是排序好的，对于冒泡排序仍然还要比较O(N^2)次，但无交换次数。可根据这个进行优化，设置一个flag，当在一趟序列中没有发生交换，则该序列已排序好，但优化后排序的时间复杂度没有发生量级的改变。
### 代码 ###
	void bubble_sort(int arr[], int len){
	//每次从后往前冒一个最小值，且每次能确定一个数在序列中的最终位置
		for (int i = 0; i < len-1; i++){         //比较n-1次
			bool exchange = true;               //冒泡的改进，若在一趟中没有发生逆序，则该序列已有序
			for (int j = len-1; j >i; j--){    // 每次从后边冒出一个最小值
				if (arr[j] < arr[j - 1]){       //发生逆序，则交换
					swap(arr[j], arr[j - 1]);
					exchange = false;
				}
			}
			if (exchange){
				return;
			}
		}
	}

## <p id="插入排序">插入排序</p> ##

### 原理 ###

依次选择一个待排序的数据，插入到前边已排好序的序列中。
### 性能 ###
时间复杂度为O(N^2)，空间复杂度为O(1)。算法是稳定的，比较次数和交换次数都与初始序列有关。

### 优化 ###
直接插入排序每次往前插入时，是按顺序依次往前找，可在这里进行优化，往前找合适的插入位置时采用二分查找的方式，即折半插入。  
**折半插入排序**相对直接插入排序而言：平均性能更快，时间复杂度降至O(NlogN)，排序是稳定的，但排序的比较次数与初始序列无关，总是需要foor(log(i))+1次排序比较。

### 使用场景 ###
当数据基本有序时，采用插入排序可以明显减少数据交换和数据移动次数，进而提升排序效率。

### 代码 ###
	void insert_sort(int arr[], int len){
	//每次把当前的数往前插入，可以顺序插入，改进的可以进行二分插入
		for (int i = 1; i < len; i++){
			if (arr[i] < arr[i - 1]){      //发生逆序，往前插入
				int temp = arr[i];
				int j;
				for (j = i - 1;j>=0 && arr[j]>temp; j--){
					arr[j+1] = arr[j];
				}
				arr[j+1] = temp;
			}
		}
	}
	
	void insert_binary_sort(int arr[], int len){
		//改进的插入排序，往前插入比较时，进行二分查找
		for (int i = 1; i < len; i++){
			if (arr[i] < arr[i - 1]){
				int temp = arr[i];
				int low = 0, high = i - 1, mid;
				while (low <= high){
					mid = (low + high) / 2;
					if (temp < arr[mid]){
						high = mid - 1;
					}
					else{
						low = mid + 1;
					}
				}
				for (int j = i; j >low; j--){
					arr[j] = arr[j - 1];
				}
				arr[low] = temp;
			}
		}
	}
	
	

## <p id="希尔排序">希尔排序</p> ##

### 原理 ###

插入排序的改进版，是基于插入排序的以下俩点性质而提出的改进方法：  

* 插入排序对几乎已排好序的数据操作时，效率很高，可以达到线性排序的效率。
* 但插入排序在每次往前插入时只能将数据移动一位，效率比较低。

所以希尔排序的思想是： 

* 先是取一个合适的gap<n作为间隔，将全部元素分为gap个子序列，所有距离为gap的元素放入同一个子序列，再对每个子序列进行直接插入排序；
* 缩小间隔gap，例如去gap=ceil(gap/2)，重复上述子序列划分和排序
* 直到，最后gap=1时，将所有元素放在同一个序列中进行插入排序为止。

### 性能 ###
开始时，gap取值较大，子序列中的元素较少，排序速度快，克服了直接插入排序的缺点；其次，gap值逐渐变小后，虽然子序列的元素逐渐变多，但大多元素已基本有序，所以继承了直接插入排序的优点，能以近线性的速度排好序。  

### 代码 ###
	void shell_sort(int arr[], int len){
		//每次选择一个gap，对相隔gap的数进行插入排序
		for (int gap = len / 2; gap > 0; gap /= 2){
			for (int i = 0; i < len; i = i + gap){
				int temp = arr[i];
				int j;
				for (j = i; j >= gap && temp < arr[j-gap]; j -= gap){
					arr[j] = arr[j - gap];
				}
				arr[j] = temp;
			}
		}
	}
	

## <p id="选择排序">选择排序</p> ##

### 原理 ###

每次从未排序的序列中找到最小值，记录并最后存放到已排序序列的末尾
### 性能 ###
时间复杂度为O(N^2)，空间复杂度为O(1)，排序是不稳定的（把最小值交换到已排序的末尾导致的），每次都能确定一个元素所在的最终位置，比较次数与初始序列无关。

### 代码 ###
	void select_sort(int arr[], int len){
		//每次从后边选择一个最小值
		for (int i = 0; i < len-1; i++){     //只需选择n-1次
			int min = i;
			for (int j = i+1; j < len; j++){
				if (arr[min]>arr[j]){
					min = j;
				}
			}
			if (min != i){
				swap(arr[i], arr[min]);
			}
		}
	}

## <p id="快速排序">快速排序</p> ##

### 原理 ###

分而治之思想：

* Divide：找到基准元素pivot，将数组A[p..r]划分为A[p..pivotpos-1]和A[pivotpos+1...q]，左边的元素都比基准小，右边的元素都比基准大;
* Conquer：对俩个划分的数组进行递归排序；
* Combine：因为基准的作用，使得俩个子数组就地有序，无需合并操作。

### 性能 ###
快排的平均时间复杂度为O(NlogN），空间复杂度为O(logN)，但最坏情况下，时间复杂度为O(N^2)，空间复杂度为O(N)；且排序是不稳定的，但每次都能确定一个元素所在序列中的最终位置，复杂度与初始序列有关。

### 优化 ###
当初始序列是非递减序列时，快排性能下降到最坏情况，主要因为基准每次都是从最左边取得，这时每次只能排好一个元素。  
所以快排的优化思路如下：

* 优化基准，不每次都从左边取，可以进行三路划分，分别取最左边，中间和最右边的中间值，再交换到最左边进行排序；或者进行随机取得待排序数组中的某一个元素，再交换到最左边，进行排序。
* 在规模较小情况下，采用直接插入排序

### 代码 ###
	//快速排序
	int partition(int arr[], const int left, const int right){
		//对序列进行划分，以第一个为基准
		int pivot = arr[left];
		int pivotpos = left;
		for (int i = left+1; i <= right; i++){
			if (arr[i] < pivot){
				pivotpos++;
				if (pivotpos != i){     //如果交换元素就位于基准后第一个，则不需要交换
					swap(arr[i], arr[pivotpos]);
				}
			}
		}
		arr[left] = arr[pivotpos];
		arr[pivotpos] = pivot;
		return pivotpos;
	}
	void quick_sort(int arr[],const int left,const int right){
		if (left < right){
			int pivotpos = partition(arr, left, right);
			quick_sort(arr, left, pivotpos - 1);
			quick_sort(arr, pivotpos + 1, right);
		}
	}
	void quick_sort(int arr[], int len){
		quick_sort(arr, 0, len - 1);
	}
	
	int improve_partition(int arr[], int left, int right){
		//基准进行随机化处理
		int n = right - left + 1;
		srand(time((unsigned)0));
		int gap = rand() % n;
		swap(arr[left], arr[left + gap]);  //把随机化的基准与左边进行交换
		//再从左边开始进行
		return partition(arr,left,right);
	}
	void quick_improve_sort(int arr[], const int left, const int right){
		//改进的快速排序
		//改进的地方：1、在规模较小时采用插入排序
		//2、基准进行随机选择
		int M = 5;
		if (right - left < M){
			insert_sort(arr, right-left+2);
		}
		if (left>=right){
			return;
		}
		int pivotpos = improve_partition(arr, left, right);
		quick_improve_sort(arr, left, pivotpos - 1);
		quick_improve_sort(arr, pivotpos + 1, right);
	}
	void quick_improve_sort(int arr[], int len){
		quick_improve_sort(arr, 0, len - 1);
	}

## <p id="归并排序">归并排序</p> ##

### 原理 ###

分而治之思想：

* Divide：将n个元素平均划分为各含n/2个元素的子序列；
* Conquer：递归的解决俩个规模为n/2的子问题；
* Combine：合并俩个已排序的子序列。

### 性能 ###
时间复杂度总是为O(NlogN)，空间复杂度也总为为O(N)，算法与初始序列无关，排序是稳定的。
### 优化 ###
优化思路：

* 在规模较小时，合并排序可采用直接插入；
* 在写法上，可以在生成辅助数组时，俩头小，中间大，这时不需要再在后边加俩个while循环进行判断，只需一次比完。

### 代码 ###
	//归并排序
	void merge(int arr[],int temp_arr[],int left,int mid, int right){
		//简单归并：先复制到temp_arr，再进行归并
		for (int i = left; i <= right; i++){
			temp_arr[i] = arr[i];
		}
		int pa = left, pb = mid + 1;
		int index = left;
		while (pa <= mid && pb <= right){
			if (temp_arr[pa] <= temp_arr[pb]){
				arr[index++] = temp_arr[pa++];
			}
			else{
				arr[index++] = temp_arr[pb++];
			}
		}
		while(pa <= mid){
			arr[index++] = temp_arr[pa++];
		}
		while (pb <= right){
			arr[index++] = temp_arr[pb++];
		}
	}
	void merge_improve(int arr[], int temp_arr[], int left, int mid, int right){
		//优化归并：复制时，俩头小，中间大，一次比较完
		for (int i = left; i <= mid; i++){
			temp_arr[i] = arr[i];
		}
		for (int i = mid + 1; i <= right; i++){
			temp_arr[i] = arr[right + mid + 1 - i];
		}
		int pa = left, pb = right, p = left;
		while (p <= right){
			if (temp_arr[pa] <= temp_arr[pb]){
				arr[p++] = temp_arr[pa++];
			}else{
				arr[p++] = temp_arr[pb--];
			}
		}
	}
	void merge_sort(int arr[],int temp_arr[], int left, int right){
		if (left < right){
			int mid = (left + right) / 2;
			merge_sort(arr,temp_arr,0, mid);
			merge_sort(arr, temp_arr,mid + 1, right);
			merge(arr,temp_arr,left,mid,right);
		}
	}
	
	void merge_sort(int arr[], int len){
		int *temp_arr = (int*)malloc(sizeof(int)*len);
		merge_sort(arr,temp_arr, 0, len - 1);
	}

## <p id="堆排序">堆排序</p> ##

### 原理 ###

堆的性质：

* 是一棵完全二叉树
* 每个节点的值都大于或等于其子节点的值，为最大堆；反之为最小堆。

堆排序思想：

* 将待排序的序列构造成一个最大堆，此时序列的最大值为根节点
* 依次将根节点与待排序序列的最后一个元素交换
* 再维护从根节点到该元素的前一个节点为最大堆，如此往复，最终得到一个递增序列

### 性能 ###
时间复杂度为O(NlogN)，空间复杂度为O(1)，因为利用的排序空间仍然是初始的序列，并未开辟新空间。算法是不稳定的，与初始序列无关。

### 使用场景 ###
想知道最大值或最小值时，比如优先级队列，作业调度等场景。

### 代码 ###
	void shiftDown(int arr[], int start, int end){  
		//从start出发到end，调整为最大堆
		int dad = start;
		int son = dad * 2 + 1;
		while (son <= end){
			//先选取子节点中较大的
			if (son + 1 <= end && arr[son] < arr[son + 1]){
				son++;
			}
			//若子节点比父节点大，则交换，继续往子节点寻找；否则退出
			if (arr[dad] < arr[son]){
				swap(arr[dad], arr[son]);
				dad = son;
				son = dad * 2 + 1;
			}
			else{
				break;
			}
		}
	}
	void heap_sort(int arr[], int len){
		//先调整为最大堆，再依次与第一个交换，进行调整，最后构成最小堆
		for (int i = (len - 2) / 2; i >= 0; i--){   //len为总长度，最后一个为len-1,所以父节点为	(len-1-1)/2
			shiftDown(arr,i,len-1);
		}
		for (int i = len - 1; i >= 0; i--){
			swap(arr[i], arr[0]);
			shiftDown(arr, 0,i-1);
		}
	}

## <p id="计数排序">计数排序</p> ##

### 原理 ###

先把每个元素的出现次数算出来，然后算出该元素所在最终排好序列中的绝对位置(最终位置)，再依次把初始序列中的元素，根据该元素所在最终的绝对位置移到排序数组中。

### 性能 ###
时间复杂度为O(N+K)，空间复杂度为O(N+K)，算法是稳定的，与初始序列无关，不需要进行比较就能排好序的算法。
### 使用场景 ###
算法只能使用在已知序列中的元素在0-k之间，且要求排序的复杂度在线性效率上。
### 代码 ###
	//计数排序
	void count_sort(int arr[],int sorted_arr[],int len,int k){
		//数组中的元素大小为0-k，
		//先统计每个数的相对位置，再算出该数所在序列中排序后的绝对位置
		int *count_arr = (int*)malloc(sizeof(int)*(k+1));
		for (int i = 0; i <= k; i++){
			count_arr[i] = 0;
		}
		for (int i = 0; i < len; i++){       //每个元素的相对位置
			count_arr[arr[i]]++;
		}
		for (int i = 1; i <= k; i++){       //每个元素的绝对位置，位置为第1个到n个
			count_arr[i] += count_arr[i - 1];
		}
		for (int i = len-1; i >=0; i--){     //从后往前，可使排序稳定，相等的俩个数的位置不会发	生逆序
			count_arr[arr[i]]--;             //把在排序后序列中绝对位置为1-n的数依次放入到0-	(n-1)中
			sorted_arr[count_arr[arr[i]]] = arr[i];
		}
		free(count_arr);
	}

## <p id="桶排序">桶排序</p> ##

### 原理 ###

* 根据待排序列元素的大小范围，均匀独立的划分M个桶
* 将N个输入元素分布到各个桶中去
* 再对各个桶中的元素进行排序
* 此时再按次序把各桶中的元素列出来即是已排序好的。
![](http://7xrhn4.com1.z0.glb.clouddn.com/bucket_sort.png)

### 性能 ###
时间复杂度为O(N+C)，O(C)=O(M*(N/M)*log(N/M))=O(N*logN-N*logM)，空间复杂度为O(N+M)，算法是稳定的，且与初始序列无关。

### 使用场景 ###
算法思想和散列中的开散列法差不多，当冲突时放入同一个桶中；可应用于数据量分布比较均匀，或比较侧重于区间数量时。

## <p id="基数排序">基数排序</p> ##
### 原理 ###
对于有d个关键字时，可以分别按关键字进行排序。有俩种方法：

* MSD：先从高位开始进行排序，在每个关键字上，可采用计数排序
* LSD：先从低位开始进行排序，在每个关键字上，可采用桶排序
![](http://7xrhn4.com1.z0.glb.clouddn.com/radix_sort.png)
### 性能 ###
时间复杂度为O(d*(N+K))，空间复杂度为O(N+K)。

## <p id="总结">总结</p> ##

以上排序算法的时间、空间与稳定性的总结如下：
<center>

|Algorithm  |Average | Best | Worst | extra space| stable|
|:----------:|:--------:|:------:|:-------:|:------------:|:-------:|
|冒泡排序  |O(N^2)|O(N)|O(N^2)|O(1)|稳定|
|直接插入排序|O(N^2)  |O(N)  |O(N^2)  |O(1)|稳定|
|折半插入排序|O(NlogN)|O(NlogN)|O(N^2)|O(1)|稳定|
|简单选择排序  |O(N^2) | O(N^2)|O(N^2)|O(1)|不稳定|
|快速排序  |O(NlogN)|O(NlogN)|O(N^2)|O(logN)~O(N^2)|不稳定|
|归并排序 |O(NlogN)|O(NlogN)|O(NlogN)|O(N)|稳定|
|堆排序   |O(NlogN)|O(NlogN)|O(NlogN)|O(1)|不稳定|
|计数排序|O(d*(N+K))|O(d*(N+K))|O(d*(N+K))|O(d*(N+K))|稳定|

</center>