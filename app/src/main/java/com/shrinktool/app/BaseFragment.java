package com.shrinktool.app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shrinktool.R;
import com.shrinktool.base.net.RestCallback;
import com.shrinktool.base.net.RestRequest;
import com.shrinktool.base.net.RestRequestManager;
import com.shrinktool.component.TitleBarHelper;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;

import butterknife.ButterKnife;

/**
 * Created by Alashi on 2015/12/18.
 */
public class BaseFragment extends Fragment{
    private static final String TAG = "BaseFragment";

    protected TitleBarHelper titleBarHelper;
    protected ProgressDialog mProgressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //即使没有菜单，也要调用setHasOptionsMenu(true)，否则会触发actionbar的bug：
        //ActionBar.NAVIGATION_MODE_LIST时，即使跑了onCreateOptionsMenu，列表和菜单可能没法显示
        setHasOptionsMenu(true);
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    protected View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, String title, @LayoutRes int resource) {
        return inflateView(inflater, container, true, title, resource);
    }

    protected View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, boolean homeButton, String title, @LayoutRes int resource) {
        RelativeLayout top = (RelativeLayout) inflater.inflate(R.layout.title_bar_fragment, container, false);
        titleBarHelper = new TitleBarHelper(getActivity(), top, homeButton);
        titleBarHelper.setTitle(title);
        inflater.inflate(resource, (LinearLayout)top.findViewById(R.id.title_bar_fragment_content), true);
        return top;
    }

    protected void replaceFragment(Class<? extends Fragment> fClass, Bundle bundle) {
        Fragment fragment = Fragment.instantiate(getContext(), fClass.getName());
        fragment.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, fragment);
        ft.commitAllowingStateLoss();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        if (titleBarHelper != null) {
            titleBarHelper.onDestroyView();
            titleBarHelper = null;
        }
        RestRequestManager.cancelAll(this);
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
    }

    public void setSupportBackButton(boolean showHomeAsUp) {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
        }
    }

    public ActionBar getActionBar() {
        return ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
    }

    public Toolbar getToolbar() {
        return  (Toolbar) getActivity().findViewById(R.id.toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public final void setTitle(int resId) {
        if (titleBarHelper != null) {
            titleBarHelper.setTitle(resId);
        } else {
            getActivity().setTitle(resId);
        }
    }

    public final void setTitle(String string) {
        if (titleBarHelper != null) {
            titleBarHelper.setTitle(string);
        } else {
            getActivity().setTitle(string);
        }
    }

    public View addMenuItem(@DrawableRes int resID, View.OnClickListener listener) {
        if (titleBarHelper != null) {
            return titleBarHelper.addMenuItem(resID, listener);
        }
        return null;
    }

    public View addMenuItem(String text, View.OnClickListener listener) {
        if (titleBarHelper != null) {
            return titleBarHelper.addMenuItem(text, listener);
        }
        return null;
    }

    public void removeAllMenu() {
        if (titleBarHelper != null) {
            titleBarHelper.removeAllMenu();
        }
    }

    public boolean isFinishing() {
        return getActivity() == null || getActivity().isFinishing();
    }

    protected final View findViewById(int id) {
        if (id < 0 || null == getView()) {
            return null;
        }

        return getView().findViewById(id);
    }

    public void showProgress(String msg) {
        if (!isAdded()) {
            return;
        }

        //布局里的进度条要比ProgressDialog启动快一些
        if (titleBarHelper != null) {
            titleBarHelper.showProgress(msg);
        } else {
            if (null == mProgressDialog) {
                mProgressDialog = new ProgressDialog(getActivity());
            }
            mProgressDialog.setMessage(msg);
            mProgressDialog.show();
        }
    }

    public void hideProgress() {
        if (titleBarHelper != null) {
            titleBarHelper.hideProgress();
        } else {
            if (null != mProgressDialog) {
                mProgressDialog.dismiss();
            }
        }
    }

    protected RestRequest executeCommand(Object command, RestCallback callback, int id) {
        return RestRequestManager.executeCommand(getActivity(), command, callback, id, this);
    }

    protected RestRequest executeCommand(Object command, RestCallback callback) {
        return RestRequestManager.executeCommand(getActivity(), command, callback, 0, this);
    }

    public void launchFragment(Class<? extends Fragment> fragment) {
        FragmentLauncher.launch(getActivity(), fragment, null);
    }

    public void launchFragment(Class<? extends Fragment> fragment, Bundle bundle) {
        FragmentLauncher.launch(getActivity(), fragment, bundle);
    }

    public void launchFragmentForResult(Class<? extends Fragment> fragment, Bundle bundle, int requestCode) {
        FragmentLauncher.launchForResult(this, fragment, bundle, requestCode);
    }

    public void showToast(CharSequence text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public void showToast(CharSequence text, int duration) {
        if (isAdded()) {
            Toast.makeText(getActivity(), text, duration).show();
        }
    }
    public void showToast(@StringRes int resId, int duration) {
        if (isAdded()) {
            Toast.makeText(getActivity(), resId, duration).show();
        }
    }

    /** 从assets目录加载文件 */
    public String loadAssets(String fileName) {
        String json = null;
        try{
            InputStream is = getContext().getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            json = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }
}
