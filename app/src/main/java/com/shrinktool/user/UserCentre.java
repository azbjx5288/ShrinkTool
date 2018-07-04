package com.shrinktool.user;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.shrinktool.base.Preferences;
import com.shrinktool.base.net.GsonHelper;
import com.shrinktool.data.LoginCommand;
import com.shrinktool.data.Lottery;
import com.shrinktool.data.UserInfo;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Alashi on 2016/1/5.
 */
public class UserCentre {
    private static final String TAG = UserCentre.class.getSimpleName();

    private static final String KEY_LOGIN_NAME = "user_login_name";
    private static final String KEY_LOGIN_PASSWORD = "user_login_password";
    private static final String KEY_LAST_LOGIN_NAME = "user_last_login_name";
    private static final String KEY_SESSION = "user_session";
    private static final String KEY_INFO = "user_info";
    private static final String KEY_LOTTERY_MAP = "user_lottery_map";
    private static final String KEY_LOTTERY_LUCREMODE = "user_lottery_lucremode_";
    private static final String KEY_LOTTERY_BONUSMODE = "user_lottery_bonusmode_";

    private static final boolean SUPPORT_AUTO_RELOAD = false;

    private Context context;
    private String session;
    private String serverBase;
    private String userID;
    private UserInfo userInfo;
    private SparseArray<Lottery> lotteryMap = new SparseArray<>();

    public UserCentre(Context context, String serverBase) {
        this.context = context;
        this.serverBase = serverBase;
        session = Preferences.getString(context, KEY_SESSION, null);
        userInfo = GsonHelper.fromJson(Preferences.getString(context, KEY_INFO, null),
                UserInfo.class);
        userID = userInfo == null ? "unknown" : String.valueOf(userInfo.getUserId());
        loadLotteryList();
    }

    public LoginCommand createLoginCommand() {
        if (!SUPPORT_AUTO_RELOAD) {
            return null;
        }
        String name = Preferences.getString(context, KEY_LOGIN_NAME, null);
        String password = Preferences.getString(context, KEY_LOGIN_PASSWORD, null);
        if (name == null || password == null) {
            return null;
        }

        LoginCommand command = new LoginCommand();
        command.setUsername(name);
        command.setEncpassword(password);
        return command;
    }

    public boolean isLogin() {
        return session != null;
    }

    public void saveSession(String session) {
        this.session = session;
        if (session == null) {
            Preferences.delete(context, KEY_SESSION);
        } else {
            Preferences.saveString(context, KEY_SESSION, session);
        }
    }

    public void saveLoginInfo(String name, String password) {
        Preferences.saveString(context, KEY_LAST_LOGIN_NAME, name);
        if (!SUPPORT_AUTO_RELOAD) {
            return;
        }
        if (name == null || password == null) {
            Preferences.delete(context, KEY_LOGIN_NAME);
            Preferences.delete(context, KEY_LOGIN_PASSWORD);
        } else {
            Preferences.saveString(context, KEY_LOGIN_NAME, name);
            Preferences.saveString(context, KEY_LOGIN_PASSWORD, password);
        }
    }

    public String getSession() {
        return session;
    }

    public String getUserID() {
        return userID;
    }

    public String getUrl(String interfaceName) {
        if (interfaceName.startsWith("http://") || interfaceName.startsWith("https://")) {
            return interfaceName;
        }

        if (TextUtils.isEmpty(interfaceName)) {
            return serverBase;
        }
        StringBuilder sb = new StringBuilder(serverBase);
        if (!serverBase.endsWith("/")) {
            sb.append("/");
        }
        if (interfaceName.startsWith("/")) {
            sb.append(interfaceName.substring(1));
        } else {
            sb.append(interfaceName);
        }
        return sb.toString();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getUserName() {
        if (userInfo != null) {
            return userInfo.getUserName();
        }
        return Preferences.getString(context, KEY_LOGIN_NAME, null);
    }

    public String getLastLoginUserName() {
        return Preferences.getString(context, KEY_LAST_LOGIN_NAME, null);
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        if (userInfo == null) {
            userID = "unknown";
            Preferences.delete(context, KEY_INFO);
        } else {
            userID = String.valueOf(userInfo.getUserId());
            Preferences.saveString(context, KEY_INFO, GsonHelper.toJson(userInfo));
        }
    }

    public void logout() {
        saveSession(null);
        setUserInfo(null);
        Preferences.delete(context, KEY_LOGIN_PASSWORD);
    }

    public void setLotteryList(ArrayList<Lottery> lotteryList) {
        if (lotteryList == null) {
            Preferences.delete(context, KEY_LOTTERY_MAP);
        } else {
            Preferences.saveString(context, KEY_LOTTERY_MAP, GsonHelper.toJson(lotteryList));
            lotteryMap.clear();
            for (Lottery lottery : lotteryList) {
                lotteryMap.put(lottery.getLotteryId(), lottery);
            }
        }
    }

    private void loadLotteryList() {
        TypeToken typeToken = new TypeToken<ArrayList<Lottery>>() {
        };
        ArrayList<Lottery> list = GsonHelper.fromJson(Preferences.getString(context, KEY_LOTTERY_MAP, null), typeToken.getType());
        lotteryMap.clear();
        if (list != null) {
            for (Lottery lottery : list) {
                lotteryMap.put(lottery.getLotteryId(), lottery);
            }
        }
    }

    public Lottery getLottery(int lotteryId) {
        return lotteryMap != null? lotteryMap.get(lotteryId) : null;
    }

    public int getLucreMode() {
        return Preferences.getInt(context, KEY_LOTTERY_LUCREMODE, 0);
    }

    public void setLucreMode(int mode) {
        Preferences.saveInt(context, KEY_LOTTERY_LUCREMODE, mode);
    }

    public int getBonusMode(int propertyId) {
        return Preferences.getInt(context, KEY_LOTTERY_BONUSMODE + propertyId, 0);
    }

    public void setBonusMode(int propertyId, int mode) {
        Preferences.saveInt(context, KEY_LOTTERY_BONUSMODE + propertyId, mode);
    }
}
