public class Solution{
	
	/**
 	 * 在一个二维数组中，每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。
     * 请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。 
  	 * @param array  [待查找的二维数组]
 	 * @param target [查找的数字]
 	 * @return       [若找到，则返回true，否则返回false]
	 */
	public boolean Find(int [][]array,int target){
		/**
		 * 查找时，从左上或右下走都无法一次性判断哪边的大小
		 * 所以，从右上或左下方向出发，以左下为例，往上走，一定更小，往右走，一定更大
		 */
		int i = array.length-1;
		int j = 0;
		while(i>=0 && j<=array[0].length-1){
			if (target==array[i][j]) {
				return true;
			}else if(target<array[i][j]){
				i--;
			}else{
				j++;
			}
		}
		return false;
	}
}