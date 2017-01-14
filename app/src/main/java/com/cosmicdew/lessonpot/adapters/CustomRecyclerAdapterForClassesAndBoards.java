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
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 18/10/16.
 */

public class CustomRecyclerAdapterForClassesAndBoards extends RecyclerView.Adapter{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static RecyclerClassBoardsListener m_cClickListener;
    private static List<BoardChoices> m_cObjBoardChoices;
    private Context m_cObjContext;
    private static boolean m_cIsEditMode;

    public CustomRecyclerAdapterForClassesAndBoards(Context pContext, List<BoardChoices> pBoardChoices,
                                                    boolean pIsEditMode,
                                                    RecyclerClassBoardsListener pListener) {
        this.m_cObjContext = pContext;
        this.m_cObjBoardChoices = pBoardChoices;
        this.m_cIsEditMode = pIsEditMode;
        this.m_cClickListener = pListener;
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
            CustomRecyclerAdapterForClassesAndBoards.DataObjectHolder ldataObjectHolder = new CustomRecyclerAdapterForClassesAndBoards.DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            CustomRecyclerAdapterForClassesAndBoards.ProgressViewHolder lprogressViewHolder = new CustomRecyclerAdapterForClassesAndBoards.ProgressViewHolder(lView);
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
                    m_cClickListener.onInfoClick(getPosition(), m_cObjBoardChoices.get(getPosition()), v);
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

        if (holder instanceof CustomRecyclerAdapterForClassesAndBoards.DataObjectHolder) {

            try {
                ((CustomRecyclerAdapterForClassesAndBoards.DataObjectHolder) holder).arrowImg
                        .setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (m_cIsEditMode) {
                try {
                    ((CustomRecyclerAdapterForClassesAndBoards.DataObjectHolder) holder).classBoardTxt
                            .setText(getString(m_cObjBoardChoices.get(position).getBoardclass().getName()) +
                                    ", " +
                                    getString(m_cObjBoardChoices.get(position).getBoardclass().getBoard().getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    ((CustomRecyclerAdapterForClassesAndBoards.DataObjectHolder) holder).classBoardTxt
                            .setText(m_cObjBoardChoices.get(position).getName() +
                                    ", " +
                                    m_cObjBoardChoices.get(position).getBoardName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                StringBuffer lBuff = new StringBuffer();
                if (getString(m_cObjBoardChoices.get(position).getSchoolName()).equals("")) {
                    lBuff.append(getString(m_cObjBoardChoices.get(position).getSchoolLocation()));
                } else {
                    lBuff.append(getString(m_cObjBoardChoices.get(position).getSchoolName()));
                    lBuff.append(getString(m_cObjBoardChoices.get(position).getSchoolLocation()).equals("") ?
                            getString(m_cObjBoardChoices.get(position).getSchoolLocation()) :
                            ", " + getString(m_cObjBoardChoices.get(position).getSchoolLocation()));
                }
                ((CustomRecyclerAdapterForClassesAndBoards.DataObjectHolder) holder).schoolNameLocTxt
                        .setText(lBuff.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((CustomRecyclerAdapterForClassesAndBoards.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private String getString(String pStr){
        return pStr != null ? pStr : "";
    }

}