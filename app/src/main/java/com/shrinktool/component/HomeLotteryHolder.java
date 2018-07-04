package com.shrinktool.component;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrinktool.R;
import com.shrinktool.app.BaseFragment;
import com.shrinktool.app.GameActivity;
import com.shrinktool.data.Lottery;
import com.shrinktool.material.ConstantInformation;

import java.util.ArrayList;

/**
 * 主页彩种的列表，支持子类型展开
 * Created by Alashi on 2017/2/14.
 */

public class HomeLotteryHolder {
    private BaseFragment fragment;
    private LinearLayout topView;

    private Holder lastExpandholder;

    public HomeLotteryHolder(BaseFragment fragment, LinearLayout topView) {
        this.fragment = fragment;
        this.topView = topView;
    }

    public void setLottery(ArrayList<Lottery> lotterys) {
        topView.removeAllViews();

        ArrayList<Holder> holders = new ArrayList<>();
        Holder group115 = new Holder();
        group115.hasSub = true;
        group115.lotterys = new ArrayList<>();
        group115.name = "11选5";
        group115.hint = "选择彩种";
        group115.resId = R.drawable.cz_11x5;
        holders.add(group115);

        for (Lottery lottery: lotterys) {
            if (lottery.getCname().contains("11选5")) {
                group115.lotterys.add(lottery);
            } else {
                Holder holder = new Holder(lottery,
                        ConstantInformation.getLotteryLogo(lottery.getLotteryId()),
                        lottery.getCname());
                holders.add(holder);
            }
        }

        LayoutInflater inflater = LayoutInflater.from(topView.getContext());
        for (int i = 0, size = (holders.size() + 1) /2; i < size; i++) {
            View top = inflater.inflate(R.layout.fragment_home_2_item, topView, false);
            if (i * 2 + 1 < holders.size()){
                inflaterItem(top, holders.get(i * 2), holders.get(i * 2 + 1));
            } else {
                inflaterItem(top, holders.get(i * 2), null);
            }
            topView.addView(top);
        }
    }

    private void inflaterItem(View top, Holder holder1, Holder holder2) {
        setData(top.findViewById(R.id.item1), holder1);
        holder1.triangle = top.findViewById(R.id.triangle);
        holder1.itemSubLayout = top.findViewById(R.id.itemSubLayout);
        holder1.itemSub = (LinearLayout) top.findViewById(R.id.itemSub);
        holder1.expandableViewHelper = new ExpandableViewHelper(holder1.itemSub);
        if (holder2 == null) {
            top.findViewById(R.id.item2).setVisibility(View.GONE);
            top.findViewById(R.id.item2replay).setVisibility(View.VISIBLE);
        } else {
            setData(top.findViewById(R.id.item2), holder2);
        }
    }

    private void setData(View item, Holder holder){
        item.setTag(holder);
        ImageView logo = (ImageView) item.findViewById(R.id.home_lottery_ico);
        TextView name = (TextView) item.findViewById(R.id.recentlyplayed_name);
        logo.setImageResource(holder.resId);
        name.setText(holder.name);
        if (holder.hint != null) {
            ((TextView) item.findViewById(R.id.recentlyplayed_tip)).setText(holder.hint);
        }
        item.setOnClickListener(clickListener);
    }

    private void closeHolder(Holder holder, Lottery lottery) {
        holder.itemSub.setTag(null);
        holder.expandableViewHelper.startCloseAnimation(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                holder.triangle.setVisibility(View.GONE);
                holder.itemSubLayout.findViewById(R.id.itemSubTop).setVisibility(View.GONE);
                holder.itemSubLayout.findViewById(R.id.itemSubBottom).setVisibility(View.GONE);
                if (lottery != null) {
                    GameActivity.launch(fragment.getActivity(), lottery.getLotteryId(), 0);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void openHolder(Holder holder){
        holder.itemSub.setTag(holder);
        holder.itemSub.removeAllViews();
        inflaterSub(holder.itemSub, holder.lotterys);
        holder.triangle.setVisibility(View.VISIBLE);
        holder.itemSubLayout.findViewById(R.id.itemSubTop).setVisibility(View.VISIBLE);
        holder.itemSubLayout.findViewById(R.id.itemSubBottom).setVisibility(View.VISIBLE);
        holder.expandableViewHelper.startOpenAnimation();
    }

    private View.OnClickListener clickListener = v -> {
        Object object = v.getTag();
        if (object == null) {
            if (lastExpandholder != null) {
                closeHolder(lastExpandholder, null);
                lastExpandholder = null;
            }
            return;
        }
        if (object instanceof  Holder) {
            Holder holder = (Holder) object;
            if (!holder.hasSub) {
                if (lastExpandholder != null) {
                    closeHolder(lastExpandholder, holder.lottery);
                    lastExpandholder = null;
                } else {
                    GameActivity.launch(fragment.getActivity(), holder.lottery.getLotteryId(), 0);
                }
            } else {
                if (holder.itemSub.getTag() != holder) {
                    openHolder(holder);
                    lastExpandholder = holder;
                } else {
                    closeHolder(holder, null);
                    lastExpandholder = null;
                }
            }
        } else {
            GameActivity.launch(fragment.getActivity(), ((Lottery)object).getLotteryId(), 0);
        }
    };

    private void inflaterSub(LinearLayout topView, ArrayList<Lottery> lotterys){
        LayoutInflater inflater = LayoutInflater.from(topView.getContext());
        for (int i = 0, size = (lotterys.size() + 1) /2; i < size; i++) {
            View top = inflater.inflate(R.layout.fragment_home_2_sub_item, topView, false);
            if (i * 2 + 1 < lotterys.size()){
                inflaterSubItem(top, lotterys.get(i * 2), lotterys.get(i * 2 + 1));
            } else {
                inflaterSubItem(top, lotterys.get(i * 2), null);
            }
            topView.addView(top);
        }
    }

    private void inflaterSubItem(View top, Lottery lottery1, Lottery lottery2) {
        setSubData(top.findViewById(R.id.item1), lottery1);
        if (lottery2 == null) {
            top.findViewById(R.id.item2).setVisibility(View.GONE);
            top.findViewById(R.id.item2replay).setVisibility(View.VISIBLE);
            top.findViewById(R.id.item2replay).setOnClickListener(clickListener);
        } else {
            setSubData(top.findViewById(R.id.item2), lottery2);
        }
    }

    private void setSubData(View item, Lottery lottery){
        item.setTag(lottery);
        ImageView logo = (ImageView) item.findViewById(R.id.home_lottery_ico);
        TextView name = (TextView) item.findViewById(R.id.recentlyplayed_name);
        logo.setImageResource(ConstantInformation.getLotteryLogo(lottery.getLotteryId()));
        name.setText(lottery.getCname());
        item.setOnClickListener(clickListener);
    }

    private class Holder{
        boolean hasSub; //是否可以展开
        String name; //显示名称
        String hint;//提示内容
        int resId; //图片id
        ArrayList<Lottery> lotterys;
        Lottery lottery;
        LinearLayout itemSub;
        View itemSubLayout;
        View triangle;
        ExpandableViewHelper expandableViewHelper;

        public Holder() {
        }

        public Holder(Lottery lottery, int resId, String name) {
            this.hasSub = false;
            this.lottery = lottery;
            this.resId = resId;
            this.name = name;
        }
    }
}
