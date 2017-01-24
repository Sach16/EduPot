package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.Chapters;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 27/10/16.
 */

public class CustomRecyclerAdapterForChapters extends RecyclerView.Adapter{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static RecyclerHomeListener m_cClickListener;
    private static List<Chapters> m_cObjChapters;
    private static String m_cSelectionType;
    private Context m_cObjContext;

    public CustomRecyclerAdapterForChapters(Context pContext, List<Chapters> pChapters, String pSellectionType, RecyclerHomeListener pListener) {
        this.m_cObjContext = pContext;
        this.m_cObjChapters = pChapters;
        this.m_cSelectionType = pSellectionType;
        this.m_cClickListener = pListener;
    }

    @Override
    public int getItemCount() {
        return m_cObjChapters.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_cObjChapters.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapters_cell, parent, false);
            CustomRecyclerAdapterForChapters.DataObjectHolder ldataObjectHolder = new CustomRecyclerAdapterForChapters.DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            CustomRecyclerAdapterForChapters.ProgressViewHolder lprogressViewHolder = new CustomRecyclerAdapterForChapters.ProgressViewHolder(lView);
            vh = lprogressViewHolder;
        }
        return vh;
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.LIST_HEADER)
        LinearLayout llUserCell;

        @Nullable
        @BindView(R.id.CLASS_BOARD_TXT)
        TextView classBoardTxt;

        @Nullable
        @BindView(R.id.SCHOOL_NAME_LOC_TXT)
        TextView schoolNameLocTxt;

        @Nullable
        @BindView(R.id.TIMESTAMP_TXT)
        TextView timeStamp;

        @Nullable
        @BindView(R.id.ARROW_CLICK_IMG)
        ImageView arrowImg;

        public DataObjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Optional
        @OnClick({R.id.LIST_HEADER})
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.LIST_HEADER:
                    if (null != CustomRecyclerAdapterForChapters.m_cSelectionType)
                        m_cClickListener.onSelectionClicked(getPosition(), null, null, m_cObjChapters.get(getPosition()), null, null, null, v);
                    else
                        m_cClickListener.onInfoClick(getPosition(), null, null, m_cObjChapters.get(getPosition()), null, null, null, v);
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

        if (holder instanceof CustomRecyclerAdapterForChapters.DataObjectHolder) {

            if (null != m_cSelectionType) {
                ((CustomRecyclerAdapterForChapters.DataObjectHolder) holder).schoolNameLocTxt.setVisibility(View.GONE);
                ((CustomRecyclerAdapterForChapters.DataObjectHolder) holder).arrowImg.setVisibility(View.GONE);
                ((CustomRecyclerAdapterForChapters.DataObjectHolder) holder).timeStamp.setVisibility(View.GONE);
            }

            try {
                String[] lTimer = PotMacros.getFormatedTimerHMS(m_cObjChapters.get(position).getLessonLength()).split(":");
                if (Integer.parseInt(lTimer[0]) > 0)
                    ((CustomRecyclerAdapterForChapters.DataObjectHolder) holder).timeStamp
                            .setText(String.format("%s h %s m %s s", lTimer[0], lTimer[1], lTimer[2]));
                else
                    ((CustomRecyclerAdapterForChapters.DataObjectHolder) holder).timeStamp
                            .setText(String.format("%s m %s s", lTimer[1], lTimer[2]));

            } catch (Exception e) {
                e.printStackTrace();
                ((CustomRecyclerAdapterForChapters.DataObjectHolder) holder).timeStamp
                        .setText("0 min 0 secs");
            }

            try {
                ((CustomRecyclerAdapterForChapters.DataObjectHolder) holder).classBoardTxt
                        .setText(getString(m_cObjChapters.get(position).getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForChapters.DataObjectHolder) holder).schoolNameLocTxt
                        .setText(String.format("%d Lessons", m_cObjChapters.get(position).getLessonCount()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((CustomRecyclerAdapterForChapters.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private String getString(String pStr){
        return pStr != null ? pStr : "";
    }

}