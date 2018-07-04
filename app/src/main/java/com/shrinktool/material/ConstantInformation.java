package com.shrinktool.material;

import android.util.SparseIntArray;

import com.shrinktool.R;

/**
 * Created on 2016/01/14.
 *
 * @author ACE
 */
public class ConstantInformation {
    private static SparseIntArray sLotteryLogo = new SparseIntArray();

    static {
        sLotteryLogo.put(1, R.drawable.cz_cqssc);
        sLotteryLogo.put(2, R.drawable.cz_sd11x5);
        sLotteryLogo.put(4, R.drawable.cz_icon_xjssc);
        sLotteryLogo.put(6, R.drawable.cz_jx11x5);
        sLotteryLogo.put(7, R.drawable.cz_gd11x5);
        sLotteryLogo.put(8, R.drawable.cz_tjssc);
        sLotteryLogo.put(9, R.drawable.cz_fc3d);
        sLotteryLogo.put(10, R.drawable.cz_tcpl3);
        sLotteryLogo.put(20, R.drawable.cz_bj11x5);
        sLotteryLogo.put(21, R.drawable.cz_sh11x5);
        sLotteryLogo.put(100, R.drawable.cz_fcssq);
    }

    public static int getLotteryLogo(int lotteryID) {
        Integer ids = sLotteryLogo.get(lotteryID);
        if (ids != null) {
            return ids;
        }
        return R.drawable.jia;
    }
}
