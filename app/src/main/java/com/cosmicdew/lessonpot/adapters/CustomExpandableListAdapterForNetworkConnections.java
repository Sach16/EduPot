package com.cosmicdew.lessonpot.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.interfaces.RecyclerNetworkListener;
import com.cosmicdew.lessonpot.models.Connections;
import com.cosmicdew.lessonpot.models.Group;
import com.cosmicdew.lessonpot.models.Role;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 25/11/16.
 */

public class CustomExpandableListAdapterForNetworkConnections extends BaseExpandableListAdapter {

    private Context m_cContext;
    private List<String> m_cListDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, Group> m_cListDataChild;
    private HashMap<String, Role> m_cListDataChildFlat;
    private static RecyclerNetworkListener m_cClickListener;
    private static boolean m_cIsFlatView;

    public CustomExpandableListAdapterForNetworkConnections(Context context, List<String> listDataHeader,
                                                            HashMap<String, Group> listChildData,
                                                            HashMap<String, Role> listDataChildFlat,
                                                            boolean pIsFlatView, RecyclerNetworkListener pListener) {
        this.m_cContext = context;
        this.m_cListDataHeader = listDataHeader;
        this.m_cListDataChild = listChildData;
        this.m_cListDataChildFlat = listDataChildFlat;
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
            convertView = inflater.inflate(R.layout.net_expandable_header_cell, null);
        }

        LinearLayout lblLLMain = (LinearLayout) convertView
                .findViewById(R.id.LIST_HEADER);
        LinearLayout lblLLParent = (LinearLayout) convertView
                .findViewById(R.id.PARENT_LL);
        ImageView lblImageView = (ImageView) convertView
                .findViewById(R.id.ARROW_CLICK_IMG);
        if (isExpanded){
//            lblLLMain.setBackgroundColor(m_cContext.getResources().getColor(R.color.white_two));
            lblImageView.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.uparrow));
        }else {
            lblImageView.setImageDrawable(m_cContext.getResources().getDrawable(R.drawable.downarrow));
//            lblLLMain.setBackgroundColor(m_cContext.getResources().getColor(R.color.white));
        }

        TextView lblListText = (TextView) convertView
                .findViewById(R.id.CLASS_BOARD_TXT);
        TextView lblListSubText = (TextView) convertView
                .findViewById(R.id.CHAPTER_RECORDING_TXT);
        if (m_cIsFlatView){
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) lblLLParent.getLayoutParams();
            params.height = 84 ; // In dp
            lblLLParent.setLayoutParams(params);
            lblListSubText.setVisibility(View.GONE);
            lblListText.setText(headerTitle);
        }else {
            String[] lStrings = headerTitle.split("#");
            lblListText.setText(lStrings[0]);
            lblListSubText.setText(lStrings[1]);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Group lGroup = null;
        Role lRole = null;
        if (m_cIsFlatView)
            lRole = (Role) getChild(groupPosition, childPosition);
        else
            lGroup = (Group) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.m_cContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.net_expandable_child_cell, null);
        }

        RecyclerView listview = (RecyclerView) convertView.findViewById(R.id.RECYC_HOME_CLASS_BOARDS);

        try {
            listview.setLayoutManager(new LinearLayoutManager(m_cContext));
            InnerListAdapter listAdapter = new  InnerListAdapter(m_cContext,
                    lGroup != null ? lGroup.getRoles() : null,
                    lRole != null ? lRole.getConnections() : null,
                    m_cIsFlatView);
            listview.setAdapter(listAdapter);
        }catch (Exception e){
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

    public class InnerListAdapter extends RecyclerView.Adapter<InnerListAdapter.DataObjectHolder>{

        private Context m_cObjContext;
        private List<Role> mObjRoleList;
        private List<Connections> mObjConnList;
        private boolean mInnerIsFlatView;

        public InnerListAdapter(Context pContext, List<Role> pObjRoles,
                                List<Connections> pObjConnList,
                                boolean pIsFlatView){
            this.m_cObjContext = pContext;
            this.mObjRoleList = pObjRoles;
            this.mObjConnList = pObjConnList;
            this.mInnerIsFlatView = pIsFlatView;
        }

        @Override
        public int getItemCount() {
            if (mInnerIsFlatView)
                return mObjConnList.size();
            else
                return mObjRoleList.size();
        }

        public class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

            @Nullable
            @BindView(R.id.CHILD_LL)
            LinearLayout childLL;

            @Nullable
            @BindView(R.id.iv_dots)
            ImageView dotsIv;

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
                switch (view.getId()){
                    case R.id.CHILD_LL:
                        if (mInnerIsFlatView) {
                        } else {
                            m_cClickListener.onInfoClick(getPosition(), mObjRoleList.get(getPosition()), null, mInnerIsFlatView, view);
                        }
                        break;
                    case R.id.iv_dots:
                        if (mInnerIsFlatView) {
                            PopupMenu pum = new PopupMenu(m_cContext, view);
                            pum.inflate(R.menu.spinner_settings_menu);
                            pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()){
                                        case R.id.action_add_syllabus:
                                            m_cClickListener.onInfoLongClick(item.getItemId(), getPosition(), null, mObjConnList.get(getPosition()), mInnerIsFlatView, view);
                                            break;
                                        case R.id.action_delete:
                                            m_cClickListener.onInfoLongClick(item.getItemId(), getPosition(), null, mObjConnList.get(getPosition()), mInnerIsFlatView, view);
                                            break;
                                    }
                                    return true;
                                }
                            });
                            pum.show();
                        } else {
                        }

                        break;
                }
            }

            @Optional
            @OnLongClick({R.id.CHILD_LL})
            public boolean onLongClick(final View view) {
                switch (view.getId()){
                    case R.id.CHILD_LL:
                        if (mInnerIsFlatView){
                        }else{
                        }
                        break;
                }
                return false;
            }
        }

        @Override
        public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_child_item, parent, false);
            DataObjectHolder ldataObjectHolder = new DataObjectHolder(lView);
            return ldataObjectHolder;
        }

        @Override
        public void onBindViewHolder(DataObjectHolder holder, int position) {
            if (mInnerIsFlatView){
                try{
                    holder.dotsIv.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    holder.leftLabel.setText(mObjConnList.get(position).getConnectionTo().getFirstName() + " " +
                            mObjConnList.get(position).getConnectionTo().getLastName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    holder.leftLabel.setText(mObjRoleList.get(position).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
