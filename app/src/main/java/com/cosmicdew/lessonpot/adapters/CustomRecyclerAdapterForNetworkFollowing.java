package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.customviews.UserCircularImageView;
import com.cosmicdew.lessonpot.interfaces.RecyclerUsersListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Follows;
import com.cosmicdew.lessonpot.models.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 22/2/17.
 */

public class CustomRecyclerAdapterForNetworkFollowing extends RecyclerView.Adapter{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static RecyclerUsersListener m_cClickListener;
    private static List<Follows> m_cObjFollows;
    private static String m_cSelectionType;
    private static Context m_cObjContext;

    public CustomRecyclerAdapterForNetworkFollowing(Context pContext, List<Follows> pFollowses, String pSellectionType,
                                                   RecyclerUsersListener pListener) {
        this.m_cObjContext = pContext;
        this.m_cObjFollows = pFollowses;
        this.m_cClickListener = pListener;
        this.m_cSelectionType = pSellectionType;
    }

    @Override
    public int getItemCount() {
        return m_cObjFollows.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_cObjFollows.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_network_cell, parent, false);
            CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder ldataObjectHolder = new CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            CustomRecyclerAdapterForNetworkFollowing.ProgressViewHolder lprogressViewHolder = new CustomRecyclerAdapterForNetworkFollowing.ProgressViewHolder(lView);
            vh = lprogressViewHolder;
        }
        return vh;
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.CV_USER_CELL)
        RelativeLayout RlUserCell;

        @Nullable
        @BindView(R.id.USER_CIV_CELL)
        UserCircularImageView uciUserPic;

        @Nullable
        @BindView(R.id.USER_FULL_NAME_TXT)
        TextView userFullNameTxt;

        @Nullable
        @BindView(R.id.USER_ROLL_TXT)
        TextView userRollTxt;

        @Nullable
        @BindView(R.id.ACCEPT_NET_TXT)
        TextView acceptNetTxt;

        @Nullable
        @BindView(R.id.DELETE_NET_TXT)
        TextView deleteNetTxt;

        @Nullable
        @BindView(R.id.SPAM_SETTINGS_IMG)
        ImageView spamSettingsImg;

        public DataObjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Optional
        @OnClick({R.id.CV_USER_CELL, R.id.SPAM_SETTINGS_IMG})
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.CV_USER_CELL:
                    if (null != CustomRecyclerAdapterForNetworkFollowing.m_cSelectionType)
                        m_cClickListener.onOptionsClick(getPosition(), m_cObjFollows.get(getPosition()), v, 0);
                    break;
                case R.id.SPAM_SETTINGS_IMG:
                    PopupMenu pum = new PopupMenu(m_cObjContext, v);
                    pum.inflate(R.menu.following_settings_menu);
                    pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.action_unfollow:
                                    m_cClickListener.onOptionsClick(getPosition(), m_cObjFollows.get(getPosition()), v, 1);
                                    break;
                            }
                            return true;
                        }
                    });
                    pum.show();
                    break;
            }
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

        if (holder instanceof CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder) {

            Users lUsers = null;

            try {
                if (CustomRecyclerAdapterForNetworkFollowing.m_cSelectionType.equals(PotMacros.FRAG_FOLLOWERS))
                    lUsers = m_cObjFollows.get(position).getFromUser();
                else
                    lUsers = m_cObjFollows.get(position).getToUser();
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder) holder).acceptNetTxt
                        .setVisibility(View.GONE);
                ((CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder) holder).deleteNetTxt
                        .setVisibility(View.GONE);
                if (CustomRecyclerAdapterForNetworkFollowing.m_cSelectionType.equals(PotMacros.FRAG_FOLLOWERS))
                    ((CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder) holder).spamSettingsImg
                            .setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Picasso.with(m_cObjContext)
                        .load(lUsers.getProfilePic())
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .fit()
                        .into(((CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder) holder).uciUserPic);
            } catch (Exception e) {
                ((CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder) holder).uciUserPic.setImageResource(R.drawable.profile_placeholder);
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder) holder).userFullNameTxt
                        .setText(getString(lUsers.getFirstName()) +
                                " " +
                                getString(lUsers.getLastName()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForNetworkFollowing.DataObjectHolder) holder).userRollTxt
                        .setText(getString(lUsers.getRole()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((CustomRecyclerAdapterForNetworkFollowing.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private String getString(String pStr){
        return pStr != null ? pStr : "";
    }

}