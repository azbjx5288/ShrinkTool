package com.shrinktool.material;

/**
 * 双色球，用于生成双色球全部注数
 * Created by Alashi on 2016/8/8.
 */
public class OriginalCode {
    private static final String TAG = "OriginalCode";

    private int[] redBall;
    private int[] blueBall;
    private int[][] outCode;
    private int outCodeIndex;

    public OriginalCode(int[] redBall, int[] blueBall) {
        this.redBall = redBall;
        this.blueBall = blueBall;
    }

    /** 红球的组合结果 */
    private static int[] C_TABLE = new int[]{
            0,0,0,0,0,0, //0个到5个
            1,7,28,84,210,//6到10
            462,924,1716,3003,5005,//11到15
            8008,12376,18564,27132,38760,//16到20
            54264,74613,100947,134596,177100,//21到25
            230230,296010,376740,475020,593775,//26到30
            736281,906192,1107568//31到33
    };

    public void combine(int[] a, int n) {
        if(null == a || a.length == 0 || n <= 0 || n > a.length) {
            return;
        }

        int[] b = new int[n];//辅助空间，保存待输出组合数
        getCombination(a, n , 0, b, 0);
    }

    private void getCombination(int[] a, int n, int begin, int[] b, int index) {

        if(n == 0){//如果够n个数了，输出b数组
            /*for(int i = 0; i < index; i++){
                System.out.print(b[i] + " ");
            }
            System.out.println();*/
            for (int aBlueBall : blueBall) {
                System.arraycopy(b, 0, outCode[outCodeIndex], 0, 6);
                outCode[outCodeIndex][6] = aBlueBall;
                //Log.i(TAG, "getCombination: " + outCodeIndex + " -> "+ Arrays.toString(outCode[outCodeIndex]));
                outCodeIndex++;
            }
            return;
        }

        for(int i = begin; i < a.length; i++){
            b[index] = a[i];
            getCombination(a, n-1, i+1, b, index+1);
        }

    }

    public int[][] getOutCode() {
        int total = C_TABLE[redBall.length] * blueBall.length;//总数
        outCode = new int[total][7];
        combine(redBall, 6);
        return outCode;
    }
}
