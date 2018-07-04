package com.shrinktool.data;

import com.google.gson.annotations.SerializedName;

/**
 * 版本信息
 * Created by Alashi on 2016/3/1.
 */
public class Version {

    /**
     * ver_number : 1.1
     * file_name : Andriod_1_1.apk
     * update_describe : 测试bata版本
     * siteMainDomain:"testxs.shdx4.com"
     */

    @SerializedName("ver_number")
    private int versionNumber;
    @SerializedName("file_name")
    private String fileName;
    @SerializedName("update_describe")
    private String updateDescribe;
    @SerializedName("isForce")
    private boolean force;
    @SerializedName("siteMainDomain")
    private String siteMainDomain;
    /** 新版本下载完整URI地址 */
    private String file;

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUpdateDescribe() {
        return updateDescribe;
    }

    public boolean isForce() {
        return force;
    }

    public String getSiteMainDomain() {
        return siteMainDomain;
    }

    public void setSiteMainDomain(String siteMainDomain) {
        this.siteMainDomain = siteMainDomain;
    }

    public String getFile() {
        return file;
    }
}
