package com.shrinktool.rule;

/**
 * Created by Alashi on 2016/5/26.
 */
public abstract class RuleObject {
    /** 所有“不限”规则都要用 UNLIMITED 表示*/
    public static final String UNLIMITED = "unlimited";
    protected final Path path;

    public RuleObject(Path path) {
        this.path = path;
        this.path.setObject(this);
    }

    public Path getPath() {
        return path;
    }

    /**
     * 对号码应用规则
     * @param numbers 号码
     * @param assist 辅助数据，若此规则需要时有效，否则null;
     * @return 号码有效
     */
    public abstract boolean apply(int[] numbers, Object assist);

    public boolean isUnlimited(){
        return getPath().toString().endsWith(UNLIMITED);
    }
}
