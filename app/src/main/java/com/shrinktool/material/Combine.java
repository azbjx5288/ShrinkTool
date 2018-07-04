package com.shrinktool.material;

/**
 * 组合生成数组
 * Created by Alashi on 2016/10/24.
 */

public class Combine {
    private static final String TAG = "Combine";

    private int[][] outCode;
    private int outCodeIndex;

    public Combine(int total, int length, int[] srcCodes) {
        outCode = new int[total][length];
        combine(srcCodes, length);
    }

    public int[][] getOutCode() {
        return outCode;
    }

    private void combine(int[] a, int n) {
        if(null == a || a.length == 0 || n <= 0 || n > a.length) {
            return;
        }

        int[] b = new int[n];//辅助空间，保存待输出组合数
        getCombination(a, n , 0, b, 0);
    }

    private void getCombination(int[] a, int n, int begin, int[] b, int index) {

        if(n == 0){//如果够n个数了，输出b数组
            //System.out.print(Arrays.toString(b));
            //System.out.println();
            System.arraycopy(b, 0, outCode[outCodeIndex], 0, b.length);
            //Log.d(TAG, "getCombination: " + Arrays.toString(outCode[outCodeIndex]));
            outCodeIndex++;
            return;
        }

        for(int i = begin; i < a.length; i++){
            b[index] = a[i];
            getCombination(a, n-1, i+1, b, index+1);
        }

    }

}
