package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.interfaces.RecyclerFilterListener;
import com.cosmicdew.lessonpot.models.Group;
import com.cosmicdew.lessonpot.models.Role;
import com.cosmicdew.lessonpot.models.RoleFilter;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.models.UsersShare;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 11/1/17.
 */

public class CustomExpandableListAdapterForLessonsFilter extends BaseExpandableListAdapter {

    private Context m_cContext;
    private List<String> m_cListDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, Group> m_cListDataChild;
    private HashMap<String, RoleFilter> m_cListDataChildFlat;
    HashMap<Integer, Object> m_cSelectionItems;
    private static RecyclerFilterListener m_cClickListener;
    private static boolean m_cIsFlatView;

    public CustomExpandableListAdapterForLessonsFilter(Context context, List<String> listDataHeader,
                                                      HashMap<String, Group> listChildData,
                                                      HashMap<String, RoleFilter> listDataChildFlat,
                                                      HashMap<Integer, Object> pSelectionItems,
                                                      boolean pIsFlatView, RecyclerFilterListener pListener) {
        this.m_cContext = context;
        this.m_cListDataHeader = listDataHeader;
        this.m_cListDataChild = listChildData;
        this.m_cListDataChildFlat = listDataChildFlat;
        this.m_cSelectionItems = pSelectionItems;
        this.m_cIsFlatView = pIsFlatView;
        this.m_cClickListener = pListener;
    }

    @Override
    public int getGroupCount() {
        return this.m_cListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.m_cListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (m_cIsFlatView)
            return this.m_cListDataChildFlat.get(this.m_cListDataHeader.get(groupPosition));
        else
            return this.m_cListDataChild.get(this.m_cListDataHeader.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.m_cContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.share_expandable_header_cell, null);
        }

        ImageView lblImageViewTick = (ImageView) convertView
                .findViewById(R.id.iv_tick);
        if (m_cIsFlatView) {
            if (((RoleFilter) m_cSelectionItems.get(groupPosition)).isSelected()) {
                lblImageViewTick.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_blue));
            } else {
                lblImageViewTick.setImageDrawable(null);
            }
        } else {
            if (((Group) m_cSelectionItems.get(groupPosition)).isSelected()) {
                lblImageViewTick.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_blue));
            } else {
                lblImageViewTick.setImageDrawable(null);
            }
        }
        LinearLayout lblLLMain = (LinearLayout) convertView
                .findViewById(R.id.LIST_HEADER);
        LinearLayout lblLLParent = (LinearLayout) convertView
                .findViewById(R.id.PARENT_LL);
        ImageView lblImageView = (ImageView) convertView
                .findViewById(R.id.ARROW_CLICK_IMG);
        if (isExpanded) {
//            lblLLMain.setBackgroundColor(m_cContext.getResources().getColor(R.color.white_two));
            lblImageView.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.uparrow));
        } else {
            lblImageView.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.downarrow));
//            lblLLMain.setBackgroundColor(m_cContext.getResources().getColor(R.color.white));
        }

        TextView lblListText = (TextView) convertView
                .findViewById(R.id.CLASS_BOARD_TXT);
        TextView lblListSubText = (TextView) convertView
                .findViewById(R.id.CHAPTER_RECORDING_TXT);
        if (m_cIsFlatView) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) lblLLParent.getLayoutParams();
            params.height = 84; // In dp
            lblLLParent.setLayoutParams(params);
            lblListSubText.setVisibility(View.GONE);
            lblListText.setText(headerTitle);
        } else {
            String[] lStrings = headerTitle.split("#");
            lblListText.setText(lStrings[0]);
            lblListSubText.setText(lStrings[1]);
        }

        return convertView;
    }

    private boolean checkAndUpdate(Object o) {
        boolean lRetVal = false;
        if (o instanceof Group) {
            Group lGroup = (Group) o;
            if (null != lGroup) {
                lRetVal = !lGroup.isSelected();
                lGroup.setSelected(lRetVal);
                for (Role lRole : lGroup.getRoles()) {
                    lRole.setSelected(lRetVal);
                    for (UsersShare lUsersShare : lRole.getUsersShares())
                        lUsersShare.setSelected(lRetVal);
                }
            }
        } else {
            Role lRole = (Role) o;
            if (null != lRole) {
                lRetVal = !lRole.isSelected();
                lRole.setSelected(lRetVal);
                for (UsersShare lUsersShare : lRole.getUsersShares())
                    lUsersShare.setSelected(lRetVal);
            }
        }
        return lRetVal;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Group lGroup = null;
        RoleFilter lRoleFilter = null;
        if (m_cIsFlatView)
            lRoleFilter = (RoleFilter) getChild(groupPosition, childPosition);
       /* else
            lGroup = (Group) getChild(groupPosition, childPosition);*/

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.m_cContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.net_expandable_child_cell, null);
        }

        RecyclerView listview = (RecyclerView) convertView.findViewById(R.id.RECYC_HOME_CLASS_BOARDS);

        try {
            listview.setLayoutManager(new LinearLayoutManager(m_cContext));
            CustomExpandableListAdapterForLessonsFilter.InnerListAdapter listAdapter = new CustomExpandableListAdapterForLessonsFilter.InnerListAdapter(m_cContext,
                    groupPosition,
                    lGroup != null ? lGroup.getRoles() : null,
                    lRoleFilter != null ? lRoleFilter.getUsers() : null,
                    m_cIsFlatView);
            listview.setAdapter(listAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class InnerListAdapter extends RecyclerView.Adapter<CustomExpandableListAdapterForLessonsFilter.InnerListAdapter.DataObjectHolder> {

        private Context m_cObjContext;
        private List<Role> mObjRoleList;
        private List<Users> mObjUsersList;
        private final int groupPosition;
        private boolean mInnerIsFlatView;

        public InnerListAdapter(Context pContext,
                                int groupPosition,
                                List<Role> pObjRoles,
                                List<Users> pObjUsersList,
                                boolean pIsFlatView) {
            this.m_cObjContext = pContext;
            this.groupPosition = groupPosition;
            this.mObjRoleList = pObjRoles;
            this.mObjUsersList = pObjUsersList;
            this.mInnerIsFlatView = pIsFlatView;
        }

        @Override
        public int getItemCount() {
            if (mInnerIsFlatView)
                return mObjUsersList.size();
            else
                return mObjRoleList.size();
        }

        public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            @Nullable
            @BindView(R.id.CHILD_LL)
            LinearLayout childLL;

            @Nullable
            @BindView(R.id.iv_tick)
            ImageView tickIv;

            @Nullable
            @BindView(R.id.text1)
            TextView leftLabel;

            public DataObjectHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @Optional
            @OnClick({R.id.CHILD_LL,
                    R.id.iv_dots})
            public void onClick(final View view) {
                switch (view.getId()) {
                    case R.id.CHILD_LL:
                        if (mInnerIsFlatView) {
                            if (((RoleFilter) (m_cSelectionItems.get(groupPosition))).getUsers().get(getPosition()).isSelected()) {
                                ((RoleFilter) (m_cSelectionItems.get(groupPosition))).getUsers().get(getPosition()).setSelected(false);
                                tickIv.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_grey));
                            }else {
                                ((RoleFilter)(m_cSelectionItems.get(groupPosition))).getUsers().get(getPosition()).setSelected(true);
                                tickIv.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_blue));
                            }
                            m_cClickListener.onInfoClick(groupPosition, getPosition(), null, mObjUsersList.get(getPosition()), mInnerIsFlatView, view);
                        } else {
                            m_cClickListener.onInfoClick(groupPosition, getPosition(), mObjRoleList.get(getPosition()), null, mInnerIsFlatView, view);
                        }
                        break;
                    case R.id.iv_tick:
                        if (mInnerIsFlatView) {
                        } else {
                        }

                        break;
                }
            }

            @Optional
            @OnLongClick({R.id.CHILD_LL})
            public boolean onLongClick(final View view) {
                switch (view.getId()) {
                    case R.id.CHILD_LL:
                        if (mInnerIsFlatView) {
                        } else {
                            if(((Group)(m_cSelectionItems.get(groupPosition))).getRoles().get(getPosition()).isSelected()) {
                                ((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(getPosition()).setSelected(false);
                                for (UsersShare lUsersShare : ((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(getPosition()).getUsersShares()) {
                                    lUsersShare.setSelected(false);
                                }
                                if (((Group) (m_cSelectionItems.get(groupPosition))).getRoles().get(getPosition()).getShares().equals(1))
                                    tickIv.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_shared));
                                else
                                    tickIv.setImageDrawable(null);
                            }else {
                                ((Group)(m_cSelectionItems.get(groupPosition))).getRoles().get(getPosition()).setSelected(true);
                                for (UsersShare lUsersShare : ((Group)(m_cSelectionItems.get(groupPosition))).getRoles().get(getPosition()).getUsersShares()) {
                                    lUsersShare.setSelected(true);
                                }
                                tickIv.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_blue));
                            }
                            m_cClickListener.onInfoLongClick(groupPosition, getPosition(), mObjRoleList.get(getPosition()), null, mInnerIsFlatView, view);
                        }
                        break;
                }
                return false;
            }
        }

        @Override
        public CustomExpandableListAdapterForLessonsFilter.InnerListAdapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View lView;
            if (mInnerIsFlatView) {
                lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_expand_child_item, parent, false);
            }else {
                lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_expand_share_child_group_item, parent, false);
            }
            CustomExpandableListAdapterForLessonsFilter.InnerListAdapter.DataObjectHolder ldataObjectHolder = new CustomExpandableListAdapterForLessonsFilter.InnerListAdapter.DataObjectHolder(lView);
            return ldataObjectHolder;
        }

        @Override
        public void onBindViewHolder(CustomExpandableListAdapterForLessonsFilter.InnerListAdapter.DataObjectHolder holder, int position) {
            if (mInnerIsFlatView) {
                try {
                    holder.tickIv.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (((RoleFilter) (m_cSelectionItems.get(groupPosition))).getUsers().get(position).isSelected()) {
                    holder.tickIv.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_blue));
                } else {
                    holder.tickIv.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_grey));
                }

                try {
                    holder.leftLabel.setText(mObjUsersList.get(position).getFirstName() + " " +
                            mObjUsersList.get(position).getLastName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } /*else {

                if (((Group) m_cSelectionItems.get(groupPosition)).getRoles().get(position).isSelected()) {
                    holder.tickIv.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_blue));
                } else {
                    if (((Group) m_cSelectionItems.get(groupPosition)).getRoles().get(position).getShares().equals(1))
                        holder.tickIv.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.tick_shared));
                    else
                        holder.tickIv.setImageDrawable(null);
                }


                try {
                    holder.leftLabel.setText(mObjRoleList.get(position).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

        }
    }
}
