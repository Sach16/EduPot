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
import com.cosmicdew.lessonpot.interfaces.RecyclerClassBoardsListener;
import com.cosmicdew.lessonpot.models.BoardChoices;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 28/10/16.
 */

public class CustomRecyclerAdapterForClasses extends RecyclerView.Adapter{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static RecyclerClassBoardsListener m_cClickListener;
    private static List<BoardChoices> m_cObjBoardChoices;
    private static String m_cSelectionType;
    private static String m_cGoOffline;
    private Context m_cObjContext;

    public CustomRecyclerAdapterForClasses(Context pContext, List<BoardChoices> pBoardChoices,String pSellectionType,
                                           String pGoOffline,
                                           RecyclerClassBoardsListener pListener) {
        this.m_cObjContext = pContext;
        this.m_cObjBoardChoices = pBoardChoices;
        this.m_cClickListener = pListener;
        this.m_cSelectionType = pSellectionType;
        this.m_cGoOffline = pGoOffline;
    }

    @Override
    public int getItemCount() {
        return m_cObjBoardChoices.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_cObjBoardChoices.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_board_cell, parent, false);
            CustomRecyclerAdapterForClasses.DataObjectHolder ldataObjectHolder = new CustomRecyclerAdapterForClasses.DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            CustomRecyclerAdapterForClasses.ProgressViewHolder lprogressViewHolder = new CustomRecyclerAdapterForClasses.ProgressViewHolder(lView);
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
                    if (null != CustomRecyclerAdapterForClasses.m_cSelectionType)
                        m_cClickListener.onSelectionClicked(getPosition(), m_cObjBoardChoices.get(getPosition()), v);
                    else
                        m_cClickListener.onInfoClick(getPosition(), m_cObjBoardChoices.get(getPosition()), v);
                    break;
            }
        }

        @Optional
        @OnLongClick({R.id.LIST_HEADER})
        public boolean onLongClick(View view) {
            switch (view.getId()) {
                case R.id.LIST_HEADER:
                    if (null == CustomRecyclerAdapterForClasses.m_cSelectionType)
                        if (!m_cObjBoardChoices.get(getPosition()).getBoardclass().getIsGeneric())
                            m_cClickListener.onInfoLongClick(getPosition(), m_cObjBoardChoices.get(getPosition()), view);
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

        if (holder instanceof CustomRecyclerAdapterForClasses.DataObjectHolder) {
            if (null != m_cSelectionType) {
                ((CustomRecyclerAdapterForClasses.DataObjectHolder) holder).schoolNameLocTxt.setVisibility(View.GONE);
                if (m_cObjBoardChoices.get(position).getBoardclass().getIsGeneric())
                    ((CustomRecyclerAdapterForClasses.DataObjectHolder) holder).arrowClickImg.setVisibility(View.GONE);

            }

            try {
                if (null != m_cGoOffline)
                    ((CustomRecyclerAdapterForClasses.DataObjectHolder) holder).schoolNameLocTxt.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (m_cObjBoardChoices.get(position).getBoardclass().getIsGeneric())
                    ((CustomRecyclerAdapterForClasses.DataObjectHolder) holder).classBoardTxt
                            .setText(getString(m_cObjBoardChoices.get(position).getBoardclass().getName()));
                else
                    ((CustomRecyclerAdapterForClasses.DataObjectHolder) holder).classBoardTxt
                            .setText(getString(m_cObjBoardChoices.get(position).getBoardclass().getName()) +
                                    ", " +
                                    getString(m_cObjBoardChoices.get(position).getBoardclass().getBoard().getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (m_cObjBoardChoices.get(position).getBoardclass().getIsGeneric()){
                    ((CustomRecyclerAdapterForClasses.DataObjectHolder) holder).schoolNameLocTxt
                            .setText(String.format("%d Lessons/Messages",
                                    m_cObjBoardChoices.get(position).getMessageCount()));
                }else {
                    ((CustomRecyclerAdapterForClasses.DataObjectHolder) holder).schoolNameLocTxt
                            .setText(String.format("%d Subjects * %d Chapters", m_cObjBoardChoices.get(position).getSyllabusCount(),
                                    m_cObjBoardChoices.get(position).getChapterCount()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((CustomRecyclerAdapterForClasses.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private String getString(String pStr){
        return pStr != null ? pStr : "";
    }

}