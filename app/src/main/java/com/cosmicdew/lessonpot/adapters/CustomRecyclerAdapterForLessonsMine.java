package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.interfaces.ItemTouchHelperAdapter;
import com.cosmicdew.lessonpot.interfaces.ItemTouchHelperViewHolder;
import com.cosmicdew.lessonpot.interfaces.OnStartDragListener;
import com.cosmicdew.lessonpot.interfaces.RecyclerHomeListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Chapters;
import com.cosmicdew.lessonpot.models.LessonShares;
import com.cosmicdew.lessonpot.models.LessonViews;
import com.cosmicdew.lessonpot.models.Lessons;
import com.cosmicdew.lessonpot.models.Syllabi;
import com.cosmicdew.lessonpot.models.Users;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by S.K. Pissay on 2/12/16.
 */

public class CustomRecyclerAdapterForLessonsMine extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static RecyclerHomeListener m_cClickListener;
    private static OnStartDragListener m_cDragStartListener;
    private static List<Lessons> m_cObjLessons;
    private static List<LessonShares> m_cObjLessonShares;
    private static List<LessonViews> m_cObjLessonViews;
    private static BoardChoices m_cBoardChoices;
    private static Users m_cUsers;
    private static Syllabi m_cSyllabi;
    private static Chapters m_cChapters;
    private static String m_cLessFromWhere;
    private static boolean m_cIsInReorder;
    private static Context m_cObjContext;

    public CustomRecyclerAdapterForLessonsMine(Context pContext, Users pUsers, BoardChoices pBoardChoices,
                                               Syllabi pSyllabi, Chapters pChapters,
                                               List<Lessons> pLessons,
                                               List<LessonShares> pLessonShares,
                                               List<LessonViews> pLessonViews,
                                               RecyclerHomeListener pListener,
                                               OnStartDragListener pDragStartListener,
                                               String pLessFromWhere,
                                               boolean pIsInReorder) {
        this.m_cObjContext = pContext;
        this.m_cObjLessons = pLessons;
        this.m_cObjLessonShares = pLessonShares;
        this.m_cObjLessonViews = pLessonViews;
        this.m_cClickListener = pListener;
        this.m_cDragStartListener = pDragStartListener;
        this.m_cUsers = pUsers;
        this.m_cBoardChoices = pBoardChoices;
        this.m_cSyllabi = pSyllabi;
        this.m_cChapters = pChapters;
        this.m_cLessFromWhere = pLessFromWhere;
        this.m_cIsInReorder = pIsInReorder;
    }

    @Override
    public int getItemCount() {
        return m_cObjLessons.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_cObjLessons.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View lView;
        // paging logic
        if (viewType == VIEW_ITEM) {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lessons_cell, parent, false);
            CustomRecyclerAdapterForLessonsMine.DataObjectHolder ldataObjectHolder = new CustomRecyclerAdapterForLessonsMine.DataObjectHolder(lView);
            vh = ldataObjectHolder;
        } else {
            lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressdialog_paging, parent, false);
            CustomRecyclerAdapterForLessonsMine.ProgressViewHolder lprogressViewHolder = new CustomRecyclerAdapterForLessonsMine.ProgressViewHolder(lView);
            vh = lprogressViewHolder;
        }
        return vh;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(m_cObjLessons, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        m_cObjLessons.remove(position);
        notifyItemRemoved(position);
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        @Nullable
        @BindView(R.id.LIST_HEADER)
        LinearLayout llUserCell;

        @Nullable
        @BindView(R.id.ROUND_CARDVIEW)
        CardView roundDotCV;

        @Nullable
        @BindView(R.id.LESSON_NAME_TXT)
        TextView lessonNameTxt;

        @Nullable
        @BindView(R.id.AUTHOR_TXT)
        TextView authorNameTxt;

        @Nullable
        @BindView(R.id.CLASS_BOARD_TXT)
        TextView classBoardTxt;

        @Nullable
        @BindView(R.id.SUB_CHAP_TXT)
        TextView subChapterTxt;

        @Nullable
        @BindView(R.id.SCHOOL_NAME_LOC_TXT)
        TextView schoolNameLocTxt;

        @Nullable
        @BindView(R.id.TIMESTAMP_TXT)
        TextView timeStamp;

        @Nullable
        @BindView(R.id.CREATEDTIME_TXT)
        TextView modifiedTime;

        public DataObjectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /*@Optional
        @OnClick({R.id.LIST_HEADER})
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.LIST_HEADER:
                    m_cClickListener.onInfoClick(getPosition(), null, null, null, m_cObjLessons.get(getPosition()),
                            m_cObjLessonShares != null ? m_cObjLessonShares.get(getPosition()) : null,
                            m_cObjLessonViews != null ? m_cObjLessonViews.get(getPosition()) : null,
                            v);
                    break;
            }
        }*/

        @Override
        public void onItemSelected() {
            if (m_cIsInReorder)
                llUserCell.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            if (m_cIsInReorder)
                llUserCell.setBackgroundColor(0);
        }

        /*@Optional
        @OnLongClick({R.id.LIST_HEADER})
        public boolean onLongClick(View view) {
            switch (view.getId()) {
                case R.id.LIST_HEADER:
                    if (null != m_cLessFromWhere && m_cLessFromWhere.equalsIgnoreCase(PotMacros.OBJ_CHAPTERS) && m_cIsInReorder) {
                        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) (view.getTag());
                        m_cDragStartListener.onStartDrag(holder, m_cObjLessons);
                    } else {
                        m_cClickListener.onInfoLongClick(getPosition(), null, null, null, m_cObjLessons.get(getPosition()),
                                m_cObjLessonShares != null ? m_cObjLessonShares.get(getPosition()) : null,
                                m_cObjLessonViews != null ? m_cObjLessonViews.get(getPosition()) : null,
                                view);
                    }
                    break;
            }
            return false;
        }*/

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CustomRecyclerAdapterForLessonsMine.DataObjectHolder) {

            /*final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                public void onLongPress(MotionEvent e) {
                    Log.e("", "Longpress detected");
                    if (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_DOWN) {
                        m_cDragStartListener.onStartDrag(holder, m_cObjLessons);
                    }
                }
            });

            if (null != m_cLessFromWhere && m_cLessFromWhere.equalsIgnoreCase(PotMacros.OBJ_CHAPTERS) && m_cIsInReorder)
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).llUserCell.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return gestureDetector.onTouchEvent(motionEvent);
                    }
                });*/

            ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).llUserCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!m_cIsInReorder)
                        m_cClickListener.onInfoClick(position, null, null, null, m_cObjLessons.get(position),
                                m_cObjLessonShares != null ? m_cObjLessonShares.get(position) : null,
                                m_cObjLessonViews != null ? m_cObjLessonViews.get(position) : null,
                                view);
                }
            });

            ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).llUserCell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (null != m_cLessFromWhere && m_cLessFromWhere.equalsIgnoreCase(PotMacros.OBJ_CHAPTERS) && m_cIsInReorder) {
                        m_cDragStartListener.onStartDrag(holder, m_cObjLessons);
                    } else {
                        m_cClickListener.onInfoLongClick(position, null, null, null, m_cObjLessons.get(position),
                                m_cObjLessonShares != null ? m_cObjLessonShares.get(position) : null,
                                m_cObjLessonViews != null ? m_cObjLessonViews.get(position) : null,
                                view);
                    }
                    return false;
                }
            });

            try {
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).roundDotCV.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String[] lTimer = PotMacros.getFormatedTimer(m_cObjLessons.get(position).getLength().getLengthSum()).split(":");
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).timeStamp
                        .setText(String.format("%s m %s s", lTimer[0], lTimer[1]));
            } catch (Exception e) {
                e.printStackTrace();
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).timeStamp
                        .setText("0 m 0 s");
            }

            try {
                if (PotMacros.getIsTodaysDate(m_cObjLessons.get(position).getModified(), PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSSSS))
                    ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).modifiedTime
                            .setText(PotMacros.getDateFormat(null, m_cObjLessons.get(position).getModified(),
                                    PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSSSS, PotMacros.TIME_FORMAT_HHMM_AM_PM));
                else
                    ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).modifiedTime
                            .setText(PotMacros.getDateFormat(null, m_cObjLessons.get(position).getModified(),
                                    PotMacros.DATE_FORMAT_UNDERSC_YYYYMMDD_HHMMSS_SSSSSS, PotMacros.DEFAULT_DATEFORMAT_DDMMYY));

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).lessonNameTxt
                        .setText(m_cObjLessons.get(position).getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).authorNameTxt
                        .setText(m_cObjLessons.get(position).getOwner().getFirstName() +
                                " " +
                                m_cObjLessons.get(position).getOwner().getLastName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).subChapterTxt
                        .setText(m_cObjLessons.get(position).getChapter().getSyllabus().getName() +
                                ", " +
                                m_cObjLessons.get(position).getChapter().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).subChapterTxt
                        .setTextColor(PotMacros.getRandomColor());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ((CustomRecyclerAdapterForLessonsMine.DataObjectHolder) holder).classBoardTxt
                        .setText(m_cObjLessons.get(position).getChapter().getSyllabus().getBoardclass().getName() +
                                ", " +
                                m_cObjLessons.get(position).getChapter().getSyllabus().getBoardclass().getBoard().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ((CustomRecyclerAdapterForLessonsMine.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public static boolean isInReorder() {
        return m_cIsInReorder;
    }

    public static void setIsInReorder(boolean m_cIsInReorder) {
        CustomRecyclerAdapterForLessonsMine.m_cIsInReorder = m_cIsInReorder;
    }
}