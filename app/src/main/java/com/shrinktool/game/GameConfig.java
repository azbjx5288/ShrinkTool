package com.shrinktool.game;

import android.util.Log;

import com.shrinktool.data.Lottery;
import com.shrinktool.data.Method;
import com.shrinktool.rule.RuleSet;

/**
 * 配置不同玩法对应的处理类
 * Created by Alashi on 2016/2/16.
 */
public class GameConfig {
    public static Game createGame(Method method) {
        //Log.d("Config", "createGame: " + GsonHelper.toJson(method));
        String name = method.getName();

        //山东11选5，除“定单双, SDDDS”
        switch (method.getLotteryId()) {
            case 2://山东11选5
            case 6://江西11选5
            case 7://广东11选5
            case 20://北京11选5
            case 21://上海11选5
            case 16://11选5分分彩
                    return new SyxwCommonGame(method);
            case 1://重庆时时彩
            case 4://新疆时时彩
            case 8://天津时时彩
            case 11://亚洲分分彩
            case 15://亚洲秒秒彩
            case 19://排序3
            //case 20://排列5
                return new SscCommonGame(method);
            case 9://福彩3D
                return new Fc3dCommonGame(method);
            case 10://P3P5(排列3)
                return new P3Game(method);
            case 100://双色球
                return new SsqGame(method);
            case 101://排列5
                return new P5Game(method);
            default:
                break;
        }
        return new NonsupportGame(method);
    }

    public static String getCategory(int lotteryId) {
        switch (lotteryId) {
            case 1://重庆时时彩
            case 4://新疆时时彩
            case 8://天津时时彩
                return "ssc";
            case 9://福彩3D
                return "fc3d";
            case 10://P3P5(排列3)
                return "pl3";
            case 2://山东11选5
            case 6://江西11选5
            case 7://广东11选5
            case 20://北京11选5
            case 21://上海11选5
                return "115";
            case 100://双色球
                return "ssq";

            default:
                return null;
        }
    }

    public static int getNumberType(Lottery lottery){
        Log.d("Config", "getNumberType lottery=" + lottery.getLotteryId());
        switch (lottery.getLotteryId()) {
            case 1://重庆时时彩
            case 4://新疆时时彩
            case 8://天津时时彩
            case 9://福彩3D
            case 10://P3P5(排列3)
                return RuleSet.TYPE_0_9_SXZX;
            case 2://山东11选5
            case 6://江西11选5
            case 7://广东11选5
            case 20://北京11选5
            case 21://上海11选5
                return RuleSet.TYPE_1_11_SDRX5;
            case 100://双色球
                return RuleSet.TYPE_SSQ;
            case 101://排列5
                return RuleSet.TYPE_WXZX;
        }
        return RuleSet.TYPE_0_9_SXZX;
    }

    public static String getLotteryName(int lotteryId) {
        switch (lotteryId) {
            case 1:
                return "重庆时时彩";
            case 4:
                return "新疆时时彩";
            case 8:
                return "天津时时彩";
            case 9:
                return "福彩3D";
            case 10:
                return "体彩排列三";
            case 100:
                return "福彩双色球";
            case 2:
                return "山东11选5";
            case 6:
                return "江西11选5";
            case 7:
                return "广东11选5";
            case 20:
                return "北京11选5";
            case 21:
                return "上海11选5";
        }
        return "未知彩种";
    }

    public static String getLotteryPlayingHelp(int lotteryId) {
        switch (lotteryId) {
            case 1:
                return "web2/cqssc_wf.html";//"重庆时时彩";
            case 4:
                return "web2/sjssc_wf.html";//"新疆时时彩";
            case 8:
                return "web2/tjssc_wf.html";//"天津时时彩";
            case 9:
                return "web2/fc3D_wf.html";//"福彩3D";
            case 10:
                return "web2/pl3_wf.html";//"体彩排列三";
            case 100:
                return "web2/ssq_wf.html";//"福彩双色球";
            case 2://山东11选5
                return "web2/sd11x5_wf.html";
            case 6://江西11选5
                return "web2/jx11x5_wf.html";
            case 7://广东11选5
                return "web2/gd11x5_wf.html";
            case 20://北京11选5
                return "web2/bj11x5_wf.html";
            case 21://上海11选5
                return "web2/sh11x5_wf.html";
        }
        return "web2/APP_index.html";//"未知彩种";
    }

    public static String getRuleHelp(int lotteryId) {
        switch (lotteryId) {
            case 1:
                return "web2/cqssc_dxjs.html";//"重庆时时彩";
            case 4:
                return "web2/sjssc_dxjs.html";//"新疆时时彩";
            case 8:
                return "web2/tjssc_dxjs.html";//"天津时时彩";
            case 9:
                return "web2/fc3D_dxjs.html";//"福彩3D";
            case 10:
                return "web2/pl3_dxjs.html";//"体彩排列三";
            case 100:
                return "web2/ssq_dxjs.html";//"福彩双色球";
            case 2://山东11选5
                return "web2/sd11x5_dxjs.html";
            case 6://江西11选5
                return "web2/jx11x5_dxjs.html";
            case 7://广东11选5
                return "web2/gd11x5_dxjs.html";
            case 20://北京11选5
                return "web2/bj11x5_dxjs.html";
            case 21://上海11选5
                return "web2/sh11x5_dxjs.html";
        }
        return "web2/APP_index.html";//"未知彩种";
    }
}
