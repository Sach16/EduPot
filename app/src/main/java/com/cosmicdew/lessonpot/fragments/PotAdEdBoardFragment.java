package com.cosmicdew.lessonpot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.interfaces.RefreshClassBoardsListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.BoardChoicesAll;
import com.cosmicdew.lessonpot.models.BoardClass;
import com.cosmicdew.lessonpot.models.BoardClassAll;
import com.cosmicdew.lessonpot.models.Sessions;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 30/12/16.
 */

public class PotAdEdBoardFragment extends PotFragmentBaseClass {

    @Nullable
    @BindView(R.id.AD_BOARDS_LL)
    ScrollView m_cLLLay;

    @Nullable
    @BindView(R.id.BOARD_TXT)
    TextView tvBoard;

    @Nullable
    @BindView(R.id.CLASS_TXT)
    TextView tvClass;

    @Nullable
    @BindView(R.id.SCHOOL_NAME_EDIT)
    EditText edSchoolName;

    @Nullable
    @BindView(R.id.SCHOOL_LOC_EDIT)
    EditText edSchoolLoc;

    private BoardChoices m_cBoardChoices;
    private ArrayList<Integer> m_cClassPosList;
    private int m_cBoardsPos = -1;
    private int m_cClassPos = -1;

    private int m_cPos = -1;

    ArrayList<String> m_cClassList;
    HashMap<String, Integer> m_cClassDic;
    ArrayList<String> m_cBoardsList;
    HashMap<String, Integer> m_cBoardsDic;

    private static RefreshClassBoardsListener m_cRefreshListener;

    public PotAdEdBoardFragment() {
        super();
    }

    public PotAdEdBoardFragment newInstance(int pPosition, BoardChoices pBoardChoices, RefreshClassBoardsListener pListener) {
        PotAdEdBoardFragment lPotAdBoardScreen = new PotAdEdBoardFragment();
        this.m_cRefreshListener = pListener;
        this.m_cBoardChoices = pBoardChoices;

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("OBJECT", (new Gson()).toJson(pBoardChoices));
        lPotAdBoardScreen.setArguments(args);

        return lPotAdBoardScreen;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        m_cIsActivityAttached = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_cObjMainView = inflater.inflate(R.layout.add_board_class_dialog, container, false);
        ButterKnife.bind(this, m_cObjMainView);

        m_cObjMainActivity.m_cObjFragmentBase = PotAdEdBoardFragment.this;

        init();

        return m_cObjMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        m_cPos = getArguments().getInt("Position");
        m_cBoardChoices = (new Gson()).fromJson(getArguments().getString("OBJECT"), BoardChoices.class);
        if (null != m_cBoardChoices) {
            tvBoard.setText(m_cBoardChoices.getBoardclass().getBoard().getName());
            tvClass.setText(m_cBoardChoices.getBoardclass().getName());
            edSchoolName.setText(m_cBoardChoices.getSchoolName());
            edSchoolLoc.setText(m_cBoardChoices.getSchoolLocation());
            ButterKnife.apply(tvBoard, PotMacros.SETENABLED, false);
            ButterKnife.apply(tvClass, PotMacros.SETENABLED, false);
        }else {
            m_cObjMainActivity.displayProgressBar(-1, "Loading");
            RequestManager.getInstance(m_cObjMainActivity).placeRequest(Constants.SYSTEMBOARDS, BoardChoicesAll.class, this, null, null, null, false);
        }
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what) {
            case R.id.BOARD_TXT:
                m_cBoardsPos = pObjMessage.arg1;
                tvBoard.setText(m_cBoardsList.get(pObjMessage.arg1));

                //calling classes api
                m_cObjMainActivity.displayProgressBar(-1, "Loading");
                HashMap<String, String> lParams = new HashMap<>();
                lParams.put(Constants.BOARD_TXT, m_cBoardsDic.get(m_cBoardsList.get(pObjMessage.arg1)).toString());
                RequestManager.getInstance(m_cObjMainActivity).placeRequest(Constants.SYSTEMBOARDCLASSES, BoardClassAll.class, this, null, lParams, null, false);
                break;
            case R.id.CLASS_TXT:
                if (pObjMessage.obj != null) {
                    m_cClassPosList = (ArrayList<Integer>) pObjMessage.obj;
                    Collections.sort(m_cClassPosList);
                    StringBuffer lBuff = new StringBuffer();
                    for (int i = 0; i < m_cClassPosList.size(); i++) {
                        if (i == 0)
                            lBuff.append(m_cClassList.get(m_cClassPosList.get(i)));
                        else
                            lBuff.append(", ").append(m_cClassList.get(m_cClassPosList.get(i)));
                    }
                    tvClass.setText(lBuff.toString());
                } else {
                    m_cClassPosList.clear();
                    m_cClassPosList.add(pObjMessage.arg1);
                    tvClass.setText(m_cClassList.get(pObjMessage.arg1));
                }
                break;
        }
    }

    @Optional
    @OnClick({R.id.BOARD_TXT, R.id.CLASS_TXT, R.id.DONE_TXT, R.id.CANCEL_TXT})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BOARD_TXT:
                if (null != m_cBoardsList && m_cBoardsList.size() > 0)
                    m_cObjMainActivity.displaySpinnerDialog(v.getId(), getResources().getString(R.string.select_board_txt), m_cBoardsList, null);
                break;
            case R.id.CLASS_TXT:
                if (null != m_cClassList && m_cClassList.size() > 0) {
                    if (m_cPos < 0 && m_cBoardChoices == null)
                        m_cObjMainActivity.displayMultiSpinnerDialog(v.getId(), getResources().getString(R.string.select_class_txt), m_cClassList, m_cClassPosList);
                    else
                        m_cObjMainActivity.displaySpinnerDialog(v.getId(), getResources().getString(R.string.select_class_txt), m_cClassList, null);
                } else
                    validate();
                break;
            case R.id.DONE_TXT:
                //TODO : Call Edit Board Class
                if (null != m_cBoardChoices) {
                    m_cObjMainActivity.displayProgressBar(-1, "Loading");
                    JSONObject lJO = new JSONObject();
                    try {
                        lJO.put(Constants.SCHOOL_NAME, edSchoolName.getText().toString().trim());
                        lJO.put(Constants.SCHOOL_LOCATION, edSchoolLoc.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    placePutRequest(Constants.USERBOARDCLASSES +
                            m_cBoardChoices.getId() +
                            "/", Sessions.class, null, null, lJO.toString(), true);
                } else {
                    if (validate()) {
                        m_cObjMainActivity.displayProgressBar(-1, "Loading");
                        ArrayList<BoardChoices> lBoardChList = new ArrayList<>();
                        for (Integer lInt : m_cClassPosList) {
                            BoardChoices lBoardChoices = new BoardChoices();
                            lBoardChoices.setBoard(m_cClassDic.get(m_cClassList.get(lInt)));
                            lBoardChoices.setBoardName(m_cBoardsList.get(m_cBoardsPos));
                            lBoardChoices.setName(m_cClassList.get(lInt));
                            lBoardChoices.setSchoolName(edSchoolName.getText().toString().trim());
                            lBoardChoices.setSchoolLocation(edSchoolLoc.getText().toString().trim());
                            lBoardChList.add(lBoardChoices);
                        }
                        JSONObject lJO = new JSONObject();
                        try {
                            lJO.put(Constants.SCHOOL_NAME, edSchoolName.getText().toString().trim());
                            lJO.put(Constants.SCHOOL_LOCATION, edSchoolLoc.getText().toString().trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (BoardChoices lBoardChoices : lBoardChList)
                            placeUserRequest(Constants.BOARDCLASSES +
                                    lBoardChoices.getBoard() +
                                    "/", Sessions.class, null, null, lJO.toString(), true);
                    }
                }
                break;
            case R.id.CANCEL_TXT:
                onBackPressed();
                break;
        }
    }

    private boolean validate() {
        boolean lRetVal = false;
        if (m_cBoardsPos < 0) {
            m_cObjMainActivity.displaySnack(m_cLLLay, "Select Board");
            return false;
        } else if (m_cClassPosList.size() <= 0) {
            m_cObjMainActivity.displaySnack(m_cLLLay, "Select Class");
            return false;
        } else {
            lRetVal = true;
        }
        return lRetVal;
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.SYSTEMBOARDS:
                BoardChoicesAll lBoardChoicesAll = (BoardChoicesAll) response;
                if (null != lBoardChoicesAll && lBoardChoicesAll.getBoardChoices().size() > 0) {
                    m_cBoardsList = new ArrayList<>();
                    m_cBoardsDic = new HashMap<>();
                    for (BoardChoices lBoardChoice : lBoardChoicesAll.getBoardChoices()) {
                        m_cBoardsList.add(lBoardChoice.getName());
                        m_cBoardsDic.put(lBoardChoice.getName(), lBoardChoice.getId());
                    }
                    if (null != m_cBoardChoices) {
                        tvBoard.setText(m_cBoardChoices.getBoardName());
                        m_cBoardsPos = m_cBoardsList.indexOf(m_cBoardChoices.getBoardName());
                    } else {
//                        tvBoard.setText(m_cBoardsList.get(0));
//                        m_cBoardsPos = 0;
                    }
                } else {
                    m_cBoardsList = new ArrayList<>();
                    m_cBoardsDic = new HashMap<>();
                    m_cBoardsList.clear();
                    m_cBoardsDic.clear();
                    tvBoard.setText(null);
                    m_cBoardsPos = -1;
                }
                m_cObjMainActivity.hideDialog();
                break;
            case Constants.SYSTEMBOARDCLASSES:
                BoardClassAll lBoardClassAll = (BoardClassAll) response;
                if (null != lBoardClassAll && lBoardClassAll.getBoardClass().size() > 0) {
                    m_cClassPosList = new ArrayList<>();
                    m_cClassList = new ArrayList<>();
                    m_cClassDic = new HashMap<>();
                    for (BoardClass lBoardClass : lBoardClassAll.getBoardClass()) {
                        m_cClassList.add(lBoardClass.getName());
                        m_cClassDic.put(lBoardClass.getName(), lBoardClass.getId());
                    }
                    if (m_cClassPosList.size() > 0) {
                        tvClass.setText(null);
                        m_cClassPosList.clear();
                    }
//                    tvClass.setText(m_cClassList.get(0));
//                    m_cClassPos = 0;
                } else {
                    m_cClassList = new ArrayList<>();
                    m_cClassDic = new HashMap<>();
                    m_cClassList.clear();
                    m_cClassDic.clear();
                    tvClass.setText(null);
                    m_cClassPosList.clear();
                }
                m_cObjMainActivity.hideDialog();
                break;
            default:
                if (apiMethod.contains(Constants.BOARDCLASSES)) {
                    Sessions lSessions = (Sessions) response;
                    if (null != lSessions) {
                        m_cObjMainActivity.hideDialog();
                        onBackPressed();
                    }
                } else {
                    m_cObjMainActivity.hideDialog();
                    super.onAPIResponse(response, apiMethod, refObj);
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            case Constants.SYSTEMBOARDS:
            case Constants.SYSTEMBOARDCLASSES:
                m_cObjMainActivity.hideDialog();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(m_cObjMainActivity, "Please check Network connection", Toast.LENGTH_SHORT).show();
                } else {
                    String lMsg = new String(error.networkResponse.data);
                    m_cObjMainActivity.showErrorMsg(lMsg);
                }
                break;
            default:
                if (apiMethod.contains(Constants.BOARDCLASSES)) {
                    m_cObjMainActivity.hideDialog();
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(m_cObjMainActivity, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    } else {
                        String lMsg = new String(error.networkResponse.data);
                        m_cObjMainActivity.showErrorMsg(lMsg);
                    }
                }else {
                    m_cObjMainActivity.hideDialog();
                    super.onErrorResponse(error, apiMethod, refObj);
                }
                break;
        }
    }

    public void onBackPressed() {
        m_cRefreshListener.resetFragment(false);
        m_cObjMainActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public int getPos() {
        return m_cPos;
    }
}
