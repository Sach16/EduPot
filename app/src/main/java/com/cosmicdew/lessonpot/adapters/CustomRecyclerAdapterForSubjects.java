package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
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
import com.cosmicdew.lessonpot.models.Syllabi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 27/10/16.
 */

public class CustomRecyclerAdapterForSubjects extends RecyclerView.Adapter{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static RecyclerHomeListener m_cClickListener;
    private static List<Syllabi> m_cObjSyllabi;
    private static String m_cSelectionType;
    private Context m_cObjContext;

    public CustomRecyclerAdapterForSubjects(Context pContext, List<Syllabi> pSyllabi, String pSellectionType, RecyclerHomeListener pListener) {
        this.m_cObjContext = pContext;
        this.m_cObjSyllabi = pSyllabi;
        this.m_cSelectionType = pSellectionType;
        this.m_cClickListener = pListener;
    }

    @Override
    public int getItemCount() {
        return m_cObjSyllabi.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_cObjSyllabi.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subjects_cell, parent, false);
            CustomRecyclerAdapterForSubjects.DataObjectHolder ldataObjectHolder = new CustomRecyclerAdapterForSubjects.DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            CustomRecyclerAdapterForSubjects.ProgressViewHolder lprogressViewHolder = new CustomRecyclerAdapterForSubjects.ProgressViewHolder(lView);
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
        @BindView(R.id.DISTINGUISHER_TXT)
        TextView distinguisherTxt;

        @Nullable
        @BindView(R.id.CHAPTER_RECORDING_TXT)
        TextView chapterRecordingTxt;
        
        @Nullable
        @BindView(R.id.ROUND_TXT)
        TextView roundText;

        @Nullable
        @BindView(R.id.ROUND_CARDVIEW)
        CardView roundImg;

        @Nullable
        @BindView(R.id.ARROW_CLICK_IMG)
        ImageView arrowClickImg;


        public DataObjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Optional
        @OnClick({R.id.LIST_HEADER})
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.LIST_HEADER:
                    if (null != CustomRecyclerAdapterForSubjects.m_cSelectionType)
                        m_cClickListener.onSelectionClicked(getPosition(), null, m_cObjSyllabi.get(getPosition()), null, null, null, null, v);
                    else
                        m_cClickListener.onInfoClick(getPosition(), null, m_cObjSyllabi.get(getPosition()), null, null, null, null, v);
                    break;
            }
        }

        @Optional
        @OnLongClick({R.id.LIST_HEADER})
        public boolean onLongClick(View view) {
            switch (view.getId()) {
                case R.id.LIST_HEADER:
                    if (null == CustomRecyclerAdapterForSubjects.m_cSelectionType)
                        if (!m_cObjSyllabi.get(getPosition()).getIsGeneric())
                            m_cClickListener.onInfoLongClick(getPosition(), null, m_cObjSyllabi.get(getPosition()), null, null, null, null, view);
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

        if (holder instanceof CustomRecyclerAdapterForSubjects.DataObjectHolder) {

            if (null != m_cSelectionType) {
                ((CustomRecyclerAdapterForSubjects.DataObjectHolder) holder).chapterRecordingTxt.setVisibility(View.GONE);
                if (m_cObjSyllabi.get(position).getIsGeneric())
                    ((CustomRecyclerAdapterForSubjects.DataObjectHolder) holder).arrowClickImg.setVisibility(View.GONE);
            }

            try {
                ((CustomRecyclerAdapterForSubjects.DataObjectHolder) holder)
                        .roundImg.setCardBackgroundColor(PotMacros.getRandomColor());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String lCh = String.valueOf(m_cObjSyllabi.get(position).getName().charAt(0));
                ((CustomRecyclerAdapterForSubjects.DataObjectHolder) holder).roundText
                        .setText(lCh);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForSubjects.DataObjectHolder) holder).classBoardTxt
                        .setText(getString(m_cObjSyllabi.get(position).getSubjectName()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*try {
                ((CustomRecyclerAdapterForSubjects.DataObjectHolder) holder).distinguisherTxt
                        .setText(getString(m_cObjSyllabi.get(position).getDistinguisher()));
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            StringBuffer lBuffer = new StringBuffer();
            if (m_cObjSyllabi.get(position).getDistinguisher().length() > 0)
                lBuffer.append(m_cObjSyllabi.get(position).getDistinguisher() + ": ");
            if (m_cObjSyllabi.get(position).getIsGeneric())
                lBuffer.append(String.format("%d Lessons",
                        m_cObjSyllabi.get(position).getMessageCount()));
            else
                lBuffer.append(String.format("%d Chapters * %d Lessons", m_cObjSyllabi.get(position).getChapterCount(),
                        m_cObjSyllabi.get(position).getLessonCount()));

            try {
                ((CustomRecyclerAdapterForSubjects.DataObjectHolder) holder).chapterRecordingTxt
                        .setText(lBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((CustomRecyclerAdapterForSubjects.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private String getString(String pStr){
        return pStr != null ? pStr : "";
    }

}