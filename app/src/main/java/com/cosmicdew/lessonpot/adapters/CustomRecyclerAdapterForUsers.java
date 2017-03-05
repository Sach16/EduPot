package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.customviews.UserCircularImageView;
import com.cosmicdew.lessonpot.interfaces.RecyclerUsersListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 5/10/16.
 */

public class CustomRecyclerAdapterForUsers extends RecyclerView.Adapter{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static RecyclerUsersListener m_cClickListener;
    private static List<Users> m_cObjJsonUsers;
    private static String m_cGoOffline;
    private Context m_cObjContext;

    public CustomRecyclerAdapterForUsers(Context pContext, List<Users> pJsonUsers,
                                         String pGoOffline,
                                         RecyclerUsersListener pListener) {
        this.m_cObjContext = pContext;
        this.m_cObjJsonUsers = pJsonUsers;
        this.m_cGoOffline = pGoOffline;
        this.m_cClickListener = pListener;
    }

    @Override
    public int getItemCount() {
        return m_cObjJsonUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_cObjJsonUsers.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_cell, parent, false);
            DataObjectHolder ldataObjectHolder = new DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            ProgressViewHolder lprogressViewHolder = new ProgressViewHolder(lView);
            vh = lprogressViewHolder;
        }
        return vh;
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.CV_USER_CELL)
        RelativeLayout cvUserCell;

        @Nullable
        @BindView(R.id.USER_CIV_CELL)
        UserCircularImageView civUser;

        @Nullable
        @BindView(R.id.USER_FULL_NAME_TXT)
        TextView fullNametxt;

        @Nullable
        @BindView(R.id.USER_ROLL_TXT)
        TextView userRoletxt;

        @Nullable
        @BindView(R.id.IMG_BADGE_TXT)
        TextView badgeTxt;

        public DataObjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Optional
        @OnClick({R.id.CV_USER_CELL})
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.CV_USER_CELL:
                    m_cClickListener.onInfoClick(getPosition(), m_cObjJsonUsers.get(getPosition()), v);
                    break;
            }
        }

        @Optional
        @OnLongClick({R.id.CV_USER_CELL})
        public boolean onLongClick(View view) {
            switch (view.getId()) {
                case R.id.CV_USER_CELL:
                    m_cClickListener.onInfoLongClick(getPosition(), m_cObjJsonUsers.get(getPosition()), view);
                    break;
            }
            return false;
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.progressBar1)
        ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DataObjectHolder) {
            try {
                Picasso.with(m_cObjContext)
                        .load(m_cObjJsonUsers.get(position).getProfilePic())
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .fit()
                        .into(((DataObjectHolder) holder).civUser);
            } catch (Exception e) {
                ((DataObjectHolder) holder).civUser.setImageResource(R.drawable.profile_placeholder);
                e.printStackTrace();
            }

            try {
                if (null != m_cGoOffline)
                    ((DataObjectHolder) holder).badgeTxt.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                int lCount = PotMacros.getNotifyCount(m_cObjContext, m_cObjJsonUsers.get(position))[0] +
                        PotMacros.getNotifyCount(m_cObjContext, m_cObjJsonUsers.get(position))[1];
                if (lCount > 0)
                    ((DataObjectHolder) holder).badgeTxt.setText(String.valueOf(lCount));
                else
                    ((DataObjectHolder) holder).badgeTxt.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((DataObjectHolder) holder).fullNametxt.setText(m_cObjJsonUsers.get(position).getFirstName() + " " + m_cObjJsonUsers.get(position).getLastName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((DataObjectHolder) holder).userRoletxt.setText(PotMacros.s2l(m_cObjJsonUsers.get(position).getRoleTitle()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

}