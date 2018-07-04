package com.shrinktool.rule;

import android.util.Log;

import com.shrinktool.rule.sdrx5.Sdrx5SourceSet;
import com.shrinktool.rule.ssc.SsqSourceSet;
import com.shrinktool.rule.ssc.SxzxSourceSet;
import com.shrinktool.rule.wxzx.WxzxSourceSet;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 管理各种“规则”
 * Created by Alashi on 2016/5/26.
 */
public class RuleManager {
    private static final String TAG = "RuleManager";
    public static final Object LOCK = new Object();

    private static RuleManager sRuleManager;
    private HashMap<String, RuleSet> mSourceMap = new LinkedHashMap<>();
    private OddVersionMather oddVersionMather;

    public static RuleManager getInstance() {
        if (sRuleManager == null) {
            sRuleManager = new RuleManager();
        }
        return sRuleManager;
    }

    public RuleManager() {
        oddVersionMather = new OddVersionMather();

        //重庆时时彩“三星直选”
        addSource(new SxzxSourceSet(Path.fromString(SxzxSourceSet.PATH_TAG)));
        //山东11选5的“任选五中五”
        addSource(new Sdrx5SourceSet(Path.fromString(Sdrx5SourceSet.PATH_TAG)));
        //双色球
        addSource(new SsqSourceSet(Path.fromString(SsqSourceSet.PATH_TAG)));
        //排列五-五星直选
        addSource(new WxzxSourceSet(Path.fromString(WxzxSourceSet.PATH_TAG)));
    }

    private void addSource(RuleSet ruleSet){
        if (ruleSet != null) {
            mSourceMap.put(ruleSet.getPath().getPrefix(), ruleSet);
        }
    }

    public RuleSet getRuleSet(Path path) {
        return (RuleSet) getRuleObject(path);
    }

    /** 将旧版本的path转换成新版本 */
    public String transformPath(String pathString) {
        return oddVersionMather.transformPath(pathString);
    }

    public RuleObject getRuleObject(Path path) {
        synchronized (LOCK) {
            String src = path.toString();
            String pathString = transformPath(src);
            if (!src.equals(pathString)) {
                //Log.i(TAG, "getRuleObject: 转换：" + src + " -> " + pathString);
                path = Path.fromString(pathString);
            }

            RuleObject obj = path.getObject();
            if (obj != null) {
                return obj;
            }

            RuleSet source = mSourceMap.get(path.getPrefix());
            if (source == null) {
                Log.w(TAG, "cannot find rule source for path: " + path);
                return null;
            }

            if (source.getPath() == path) {
                return source;
            }

            try {
                if (RuleObject.UNLIMITED.equals(path.getSuffix())) {
                    return new UnlimitedRuleItem(path);
                }
                RuleSet parentRuleSet = findParentRuleSet(path);
                RuleObject object = parentRuleSet.createRuleObject(path);
                if (object == null) {
                    Log.w(TAG, "cannot create rule object: " + path);
                }
                return object;
            } catch (Throwable t) {
                Log.w(TAG, "exception in creating rule object: " + path, t);
                return null;
            }
        }
    }

    /** 找到Path对应的上一层RuleSet */
    private RuleSet findParentRuleSet(Path path) {
        Path parentPath  = path.getParent();
        if (parentPath == null) {
            return null;
        }
        if (parentPath.getObject() != null) {
            return (RuleSet) parentPath.getObject();
        } else {
            RuleSet parentRuleSet = findParentRuleSet(parentPath);
            if (parentRuleSet == null) {
                return null;
            }
            return (RuleSet) parentRuleSet.createRuleObject(path);
        }
    }
}
