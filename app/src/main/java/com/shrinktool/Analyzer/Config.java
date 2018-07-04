package com.shrinktool.Analyzer;

/**
 * 配置彩种(玩法)对应的TrendProcessor
 * Created by Alashi on 2016/12/5.
 */

public final class Config {

    public static TrendProcessor getTrendProcessor(int lotteryId){
        switch (lotteryId) {
            case 1: //重庆时时彩，玩法1的开奖号码是5个，玩法用后3个
                return new TrendProcessor1(lotteryId, 1);
            case 4: //新疆时时彩，玩法103的开奖号码是5个，玩法用后3个
                return new TrendProcessor1(lotteryId, 103);
            case 8: //天津时时彩，玩法103的开奖号码是5个，玩法用后3个
                return new TrendProcessor1(lotteryId, 190);
            case 9: //福彩3D
                return new TrendProcessor1(lotteryId, 231);
            case 10: //体彩排列3
                return new TrendProcessor1(lotteryId, 246);
            case 100: //福彩双色球
                return new TrendProcessorSsq(lotteryId, 773);
            case 2://山东11选5
                return new TrendProcessorSDRX5(lotteryId, 16);
            case 6://江西11选5
                return new TrendProcessorSDRX5(lotteryId, 166);
            case 7://广东11选5
                return new TrendProcessorSDRX5(lotteryId, 182);
            case 20://北京11选5
                return new TrendProcessorSDRX5(lotteryId, 671);
            case 21://上海11选5
                return new TrendProcessorSDRX5(lotteryId, 687);
        }
        return null;
    }
}
