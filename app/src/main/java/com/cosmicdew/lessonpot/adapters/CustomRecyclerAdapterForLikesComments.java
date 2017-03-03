package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.customviews.UserCircularImageView;
import com.cosmicdew.lessonpot.interfaces.RecyclerLikeCommentListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Comments;
import com.cosmicdew.lessonpot.models.Likes;
import com.cosmicdew.lessonpot.models.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 16/2/17.
 */

public class CustomRecyclerAdapterForLikesComments extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private String LOG_TAG = "MyRecyclerViewAdapter";
    private RecyclerLikeCommentListener m_cClickListener;
    private Users m_cUsers;
    private List<Comments> m_cComments;
    private List<Likes> m_cLikes;
    private boolean m_cMode;
    private String m_cUserMode;
    private Context m_cObjContext;

    public CustomRecyclerAdapterForLikesComments(Context pContext,
                                                 Users pUsers,
                                                 List<Likes> pLikes,
                                                 List<Comments> pComments,
                                                 boolean pMode,
                                                 String pUserMode,
                                                 RecyclerLikeCommentListener pListener) {
        this.m_cObjContext = pContext;
        this.m_cUsers = pUsers;
        this.m_cComments = pComments;
        this.m_cLikes = pLikes;
        this.m_cMode = pMode;
        this.m_cUserMode = pUserMode;
        this.m_cClickListener = pListener;
    }

    @Override
    public int getItemCount() {
        if (m_cMode)
            return m_cLikes.size();
        else
            return m_cComments.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (m_cMode)
            return m_cLikes.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        else
            return m_cComments.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_likes_comments_cell, parent, false);
            CustomRecyclerAdapterForLikesComments.DataObjectHolder ldataObjectHolder = new CustomRecyclerAdapterForLikesComments.DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            CustomRecyclerAdapterForLikesComments.ProgressViewHolder lprogressViewHolder = new CustomRecyclerAdapterForLikesComments.ProgressViewHolder(lView);
            vh = lprogressViewHolder;
        }
        return vh;
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        @BindView(R.id.USER_ROLL_OR_COMMENT_TXT)
        TextView userRollCommentTxt;

        @Nullable
        @BindView(R.id.TIMESTAMP_TXT)
        TextView timeStampTxt;

        @Nullable
        @BindView(R.id.LESSON_NAME_TXT)
        TextView lessonNameTxt;

        public DataObjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Optional
        @OnClick({R.id.CV_USER_CELL, R.id.USER_CIV_CELL,
                R.id.USER_FULL_NAME_TXT})
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.CV_USER_CELL:
                    if (m_cMode)
                        m_cClickListener.onInfoClick(getPosition(), m_cLikes.get(getPosition()), null, m_cMode, v);
                    else
                        m_cClickListener.onInfoClick(getPosition(), null, m_cComments.get(getPosition()), m_cMode, v);
                    break;
                case R.id.USER_CIV_CELL:
                case R.id.USER_FULL_NAME_TXT:
                    break;
                case R.id.SPAM_SETTINGS_IMG:

                    break;
            }
        }

        @Optional
        @OnLongClick({R.id.CV_USER_CELL})
        public boolean onLongClick(final View v) {
            switch (v.getId()) {
                case R.id.CV_USER_CELL:
                    if (m_cMode) {
                    } else {
                        if (m_cComments.get(getPosition()).getUser().getId() == m_cUsers.getId()) {
                            PopupMenu pum = new PopupMenu(m_cObjContext, v);
                            pum.inflate(R.menu.nav_comments_menu);
                            pum.getMenu().findItem(R.id.action_report_spam).setVisible(false);
                            pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.action_edit:
                                            m_cClickListener.onInfoLongClick(getPosition(), null, m_cComments.get(getPosition()), m_cMode, 0, v);
                                            break;
                                        case R.id.action_delete:
                                            m_cClickListener.onInfoLongClick(getPosition(), null, m_cComments.get(getPosition()), m_cMode, 1, v);
                                            break;
                                        case R.id.action_report_spam:
                                            m_cClickListener.onInfoLongClick(getPosition(), null, m_cComments.get(getPosition()), m_cMode, 2, v);
                                            break;

                                    }
                                    return true;
                                }
                            });
                            pum.show();
                        } else /*if (m_cComments.get(getPosition()).getLesson().getOwner().getId() == m_cUsers.getId())*/ {
                            PopupMenu pum = new PopupMenu(m_cObjContext, v);
                            pum.inflate(R.menu.nav_comments_menu);
                            pum.getMenu().findItem(R.id.action_edit).setVisible(false);
                            pum.getMenu().findItem(R.id.action_delete).setVisible(false);
                            pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.action_edit:
                                            m_cClickListener.onInfoLongClick(getPosition(), null, m_cComments.get(getPosition()), m_cMode, 0, v);
                                            break;
                                        case R.id.action_delete:
                                            m_cClickListener.onInfoLongClick(getPosition(), null, m_cComments.get(getPosition()), m_cMode, 1, v);
                                            break;
                                        case R.id.action_report_spam:
                                            m_cClickListener.onInfoLongClick(getPosition(), null, m_cComments.get(getPosition()), m_cMode, 2, v);
                                            break;

                                    }
                                    return true;
                                }
                            });
                            pum.show();
                        }
                    }
                    break;
            }
            return false;
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {

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

        if (holder instanceof CustomRecyclerAdapterForLikesComments.DataObjectHolder) {
            try {
                String lPicUrl;
                if (m_cMode)
                    lPicUrl = m_cLikes.get(position).getUser().getProfilePic();
                else
                    lPicUrl = m_cComments.get(position).getUser().getProfilePic();

                Picasso.with(m_cObjContext)
                        .load(lPicUrl)
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .fit()
                        .into(((CustomRecyclerAdapterForLikesComments.DataObjectHolder) holder).uciUserPic);
            } catch (Exception e) {
                ((CustomRecyclerAdapterForLikesComments.DataObjectHolder) holder).uciUserPic.setImageResource(R.drawable.profile_placeholder);
                e.printStackTrace();
            }

            try {
                String lUserName;
                if (m_cMode)
                    lUserName = m_cLikes.get(position).getUser().getFirstName() + " " + m_cLikes.get(position).getUser().getLastName();
                else
                    lUserName = m_cComments.get(position).getUser().getFirstName() + " " + m_cComments.get(position).getUser().getLastName();
                ((CustomRecyclerAdapterForLikesComments.DataObjectHolder) holder).userFullNameTxt
                        .setText(lUserName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String lRoleOrComm;
                if (m_cMode)
                    lRoleOrComm = m_cLikes.get(position).getUser().getRoleTitle();
                else
                    lRoleOrComm = m_cComments.get(position).getComment();
                ((CustomRecyclerAdapterForLikesComments.DataObjectHolder) holder).userRollCommentTxt
                        .setText(lRoleOrComm);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String lTimeStamp = null;
                if (m_cMode)
                    lTimeStamp = m_cLikes.get(position).getTime();
                else
                    lTimeStamp = m_cComments.get(position).getModified();
                    
                if (PotMacros.getIsTodaysDate(lTimeStamp, PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSSSS))
                    ((CustomRecyclerAdapterForLikesComments.DataObjectHolder) holder).timeStampTxt
                            .setText(PotMacros.getDateFormat(null, lTimeStamp,
                                    PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSSSS, PotMacros.TIME_FORMAT_HHMM_AM_PM));
                else
                    ((CustomRecyclerAdapterForLikesComments.DataObjectHolder) holder).timeStampTxt
                            .setText(PotMacros.getDateFormat(null, lTimeStamp,
                                    PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSSSS, PotMacros.DEFAULT_DATEFORMAT_DDMMYY));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String lLessonName = null;
                if (m_cMode)
                    lLessonName = m_cLikes.get(position).getLesson().getName();
                else
                    lLessonName = m_cComments.get(position).getLesson().getName();
                if (null != m_cUserMode) {
                    ((CustomRecyclerAdapterForLikesComments.DataObjectHolder) holder).lessonNameTxt
                            .setText(lLessonName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((CustomRecyclerAdapterForLikesComments.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private String getString(String pStr) {
        return pStr != null ? pStr : "";
    }

}