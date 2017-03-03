package com.cosmicdew.lessonpot.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotBaseActivity;
import com.cosmicdew.lessonpot.customviews.TouchImageView;
import com.cosmicdew.lessonpot.customviews.UserCircularImageView;
import com.cosmicdew.lessonpot.fragments.PotGlobalSettingsFragment;
import com.cosmicdew.lessonpot.fragments.PotProfileInfoFragment;
import com.cosmicdew.lessonpot.fragments.PotRegisterPhoneNumbers;
import com.cosmicdew.lessonpot.fragments.PotUserCredentialsFragment;
import com.cosmicdew.lessonpot.interfaces.RefreshEditProfileListener;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.cosmicdew.lessonpot.models.BoardChoices;
import com.cosmicdew.lessonpot.models.Users;
import com.cosmicdew.lessonpot.network.Constants;
import com.cosmicdew.lessonpot.network.RequestManager;
import com.cosmicdew.lessonpot.utils.Utilities;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by S.K. Pissay on 28/12/16.
 */

public class EditProfileScreen extends PotBaseActivity implements RefreshEditProfileListener {

    @Nullable
    @BindView(R.id.MAIN_RL)
    CoordinatorLayout m_cMainCL;

    @Nullable
    @BindView(R.id.EDIT_PRO_MAIN_RL)
    RelativeLayout m_cMainReLay;

    @Nullable
    @BindView(R.id.USER_CIV_CELL)
    UserCircularImageView m_cUserCivCell;

    @Nullable
    @BindView(R.id.FIRST_NAME)
    EditText etFirstName;

    @Nullable
    @BindView(R.id.LAST_NAME)
    EditText etLastName;

    @Nullable
    @BindView(R.id.ROLE_TXT)
    TextView m_cRoleTxt;

    @Nullable
    @BindView(R.id.FOLLOW_TXT)
    TextView m_cFollowTxt;

    @Nullable
    @BindView(R.id.BOTTOM_VIEW)
    FrameLayout viewStub;

    @Nullable
    @BindView(R.id.USER_IMG)
    TouchImageView bottomImg;

    BottomSheetBehavior<FrameLayout> m_cSlider;

    private Menu mMenu;
    ArrayList<BoardChoices> m_cBoardChoiceList;

    public static final String FRAG_CREDENTIALS_FRAGMENT = "FRAG_CREDENTIALS_FRAGMENT";
    public static final String FRAG_REG_PHONENUMBERS = "FRAG_REG_PHONENUMBERS";
    public static final String FRAG_GLOBAL_SETTINGS = "FRAG_GLOBAL_SETTINGS";
    public static final String FRAG_PROFILE_INFO = "FRAG_PROFILE_INFO";

    public static final int TAKE_PICTURE = 101;
    public static final int PROCESS_PICTURE = 111;

    public static final int CALL_PROCESS_PICTURE = 171;
    public static final int CALL_FIRST_NAME = 172;
    public static final int CALL_LAST_NAME = 173;

    private int m_cCurrentImgId;

    boolean m_cImageProcessing;
    private Bitmap m_cObjSelectedBitMap;

    private Users m_cUser;

    @Override
    protected void onCreate(Bundle pSavedInstance) {
        super.onCreate(pSavedInstance);
        setContentView(R.layout.edit_profile_screen);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(getResources().getString(R.string.edit_profile_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        if (null != getIntent().getStringExtra(PotMacros.OBJ_USER)) {
            m_cUser = (new Gson()).fromJson(getIntent().getStringExtra(PotMacros.OBJ_USER), Users.class);
            if (null != m_cUser) {
                try {
                    Picasso.with(EditProfileScreen.this)
                            .load(m_cUser.getProfilePic())
                            .error(R.drawable.profile_placeholder)
                            .placeholder(R.drawable.profile_placeholder)
                            .config(Bitmap.Config.RGB_565)
                            .fit()
                            .into(m_cUserCivCell);
                }catch (Exception e){
                    e.printStackTrace();
                }
                etFirstName.setText(m_cUser.getFirstName());
                etFirstName.setSelection(m_cUser.getFirstName().length());
                etFirstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_SEARCH ||
                                i == EditorInfo.IME_ACTION_DONE ||
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                        keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            Log.w("EDITTEXT : ", "afterTextChanged");
                            m_cObjUIHandler.sendEmptyMessage(CALL_FIRST_NAME);
                        }
                        return false;
                    }
                });
                etLastName.setText(m_cUser.getLastName());
                etLastName.setSelection(m_cUser.getLastName().length());
                etLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_SEARCH ||
                                i == EditorInfo.IME_ACTION_DONE ||
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                        keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            Log.w("EDITTEXT : ", "afterTextChanged");
                            m_cObjUIHandler.sendEmptyMessage(CALL_LAST_NAME);
                        }
                        return false;
                    }
                });
                m_cRoleTxt.setText(m_cUser.getRoleTitle());
            }
        }
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {
        switch (pObjMessage.what){
            case R.id.PROFILE_PIC_LL:
                m_cCurrentImgId = pObjMessage.what;
                performPhotoOptions(pObjMessage.arg1, PotMacros.LESSON_IMG_2, m_cUserCivCell);
                break;
            case PROCESS_PICTURE:
                new CaptureImage().execute("");
                break;
            case CALL_PROCESS_PICTURE:
                if ((new File(PotMacros.getImageFilePath(EditProfileScreen.this), m_cImageGUID + ".jpg").exists())) {
                    HashMap<String, String> lParams = new HashMap<>();
                    lParams.put(Constants.FIRST_NAME, etFirstName.getText().toString().trim());
                    lParams.put(Constants.LAST_NAME, etLastName.getText().toString().trim());
                    lParams.put(Constants.ROLE, m_cUser.getRole());
                    lParams.put(Constants.TERMS_ACCEPTED, String.valueOf(m_cUser.getTermsAccepted()));
                    RequestManager.getInstance(this).placeUnivMultiPartRequest(Constants.USERS +
                                    m_cUser.getId() +
                                    "/" , Users.class, this, CALL_PROCESS_PICTURE, lParams,
                            new File(PotMacros.getImageFilePath(EditProfileScreen.this), m_cImageGUID + ".jpg"),
                            Constants.PROFILE_PIC, Request.Method.PATCH);
                }
                break;
            case CALL_FIRST_NAME:
            case CALL_LAST_NAME:
                JSONObject lJO = new JSONObject();
                try {
                    lJO.put(Constants.FIRST_NAME, etFirstName.getText().toString().trim());
                    lJO.put(Constants.LAST_NAME, etLastName.getText().toString().trim());
                    lJO.put(Constants.ROLE, m_cUser.getRole());
                    lJO.put(Constants.TERMS_ACCEPTED, String.valueOf(m_cUser.getTermsAccepted()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestManager.getInstance(this).placeUnivRequest(Constants.USERS +
                        m_cUser.getId() +
                        "/", Users.class, this, CALL_PROCESS_PICTURE, null, lJO.toString(), Request.Method.PUT);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteUser() {
        displayProgressBar(-1, "");
        RequestManager.getInstance(this).placeDeleteRequest(Constants.USERS + m_cUser.getId(), Users.class, this, null, null, null, true);
    }

    @Override
    public void onUpdate(int pPostion, Users pUsers, View pView) {
        switch (pPostion){
            case 0:
                break;
            case 1:
                break;
            case 2:
                m_cUser = pUsers;
                break;
        }
    }

    public class DeleteUser extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (null != m_cUser) {
                try {
                    URL url = new URL("http://52.66.119.49/api/v1/" + Constants.USERS + m_cUser.getId());
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
//                    httpCon.setDoOutput(true);
                    httpCon.setRequestProperty(
                            "Content-Type", "application/x-www-form-urlencoded");
                    httpCon.setRequestMethod("DELETE");
                    httpCon.setRequestProperty(Constants.AUTHORIZATION, PotMacros.getLoginAuth(EditProfileScreen.this));
                    httpCon.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Optional
    @OnClick({R.id.PROFILE_PIC_LL,
            R.id.CLASS_BOARD_TXT_RL,
            R.id.USER_CRED_TXT_RL,
            R.id.REG_PH_TXT_RL,
            R.id.SETTINGS_RL,
            R.id.PROFILE_INFO_RL,
            R.id.ROLE_TXT,
            R.id.PAGER_DELETE_IMG})
    public void onClick(View v) {
        Intent lObjIntent;
        switch (v.getId()) {
            case R.id.PROFILE_PIC_LL:
                if (m_cUser.getProfilePic() != null)
                    showPhotoOption(v.getId(), true);
                else
                    showPhotoOption(v.getId(), false);
                break;
            case R.id.CLASS_BOARD_TXT_RL:
                lObjIntent = new Intent(this, PotEditBoardClassScreen.class);
                lObjIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
                startActivity(lObjIntent);
                break;
            case R.id.USER_CRED_TXT_RL:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.EDIT_PRO_MAIN_RL, new PotUserCredentialsFragment().newInstance(0, m_cUser, this), FRAG_CREDENTIALS_FRAGMENT)
                        .commit();
                switchModeFragment(true, FRAG_CREDENTIALS_FRAGMENT);
                break;
            case R.id.REG_PH_TXT_RL:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.EDIT_PRO_MAIN_RL, new PotRegisterPhoneNumbers().newInstance(1, m_cUser, this), FRAG_REG_PHONENUMBERS)
                        .commit();
                switchModeFragment(true, FRAG_REG_PHONENUMBERS);
                break;
            case R.id.SETTINGS_RL:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.EDIT_PRO_MAIN_RL, new PotGlobalSettingsFragment().newInstance(2, m_cUser, this), FRAG_GLOBAL_SETTINGS)
                        .commit();
                switchModeFragment(true, FRAG_GLOBAL_SETTINGS);
                break;
            case R.id.PROFILE_INFO_RL:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.EDIT_PRO_MAIN_RL, new PotProfileInfoFragment().newInstance(2, m_cUser, this), FRAG_PROFILE_INFO)
                        .commit();
                switchModeFragment(true, FRAG_PROFILE_INFO);
                break;
            case R.id.PAGER_DELETE_IMG:
                viewStub.setVisibility(View.GONE);
                break;
        }
    }

    private void switchModeFragment(boolean pIsFragment, String pFragTag) {
        switch (pFragTag){
            case FRAG_CREDENTIALS_FRAGMENT:
                getSupportActionBar().setTitle(getResources().getString(R.string.user_credentials_txt));
                break;
            case FRAG_REG_PHONENUMBERS:
                getSupportActionBar().setTitle(getResources().getString(R.string.registered_phone_numbers_txt));
                break;
            case FRAG_GLOBAL_SETTINGS:
                getSupportActionBar().setTitle(getResources().getString(R.string.global_permission_settings_txt));
                break;
            case FRAG_PROFILE_INFO:
                getSupportActionBar().setTitle(getResources().getString(R.string.profile_information_txt));
                break;
            default:
                getSupportActionBar().setTitle(getResources().getString(R.string.edit_profile_txt));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(FRAG_CREDENTIALS_FRAGMENT) != null) {
            ((PotUserCredentialsFragment) getSupportFragmentManager().findFragmentByTag(FRAG_CREDENTIALS_FRAGMENT)).onBackPressed();
            switchModeFragment(false, "");
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_REG_PHONENUMBERS) != null) {
            ((PotRegisterPhoneNumbers) getSupportFragmentManager().findFragmentByTag(FRAG_REG_PHONENUMBERS)).onBackPressed();
            switchModeFragment(false, "");
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_GLOBAL_SETTINGS) != null) {
            ((PotGlobalSettingsFragment) getSupportFragmentManager().findFragmentByTag(FRAG_GLOBAL_SETTINGS)).onBackPressed();
            switchModeFragment(false, "");
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_PROFILE_INFO) != null) {
            ((PotProfileInfoFragment) getSupportFragmentManager().findFragmentByTag(FRAG_PROFILE_INFO)).onBackPressed();
            switchModeFragment(false, "");
        } else {
            Intent lReturnIntent = new Intent();
            lReturnIntent.putExtra(PotMacros.OBJ_USER, (new Gson()).toJson(m_cUser));
            setResult(Activity.RESULT_OK, lReturnIntent);
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        displayProgressBar(-1, "Loading...");
                        long ldate = 0;
                        Uri lUriwodata = null;
                        try {
                            lUriwodata = data.getData();
                        } catch (Exception e) {
                            lUriwodata = null;
                        }
                        if (data != null && lUriwodata != null) {
                            File lfile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");

                            Uri m_cSelectedImageUri = data == null ? null : data.getData();
                            String lImageName = m_cImageGUID + ".jpg";

                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getContentResolver().query(m_cSelectedImageUri,
                                    filePathColumn, null, null, null);
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();

                            copyFile(picturePath, lfile.getAbsolutePath());

                            m_cObjUIHandler.sendEmptyMessageDelayed(PROCESS_PICTURE, 500);
                        } else {
                            m_cObjUIHandler.sendEmptyMessageDelayed(PROCESS_PICTURE, 500);

                            File lFile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");
                            double kilobytes = (lFile.length() / 1024);
                            if (kilobytes > 2000) {
                                displaySnack(m_cMainCL, "Selected file exceeds upload file limit");
                            }
                        }
//						deletePicFromDCIM();
                    } catch (Exception e) {
                        displaySnack(m_cMainCL, "Unable to retrieve Image");
                        e.printStackTrace();
                        Log.w("IMAGE_NAME  : ", m_cImageGUID);
                    }
                }
                break;
        }
    }

    @Override
    public void onAPIResponse(Object response, String apiMethod, Object refObj) {
        Intent lObjIntent;
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.USERS)){
                    Users lUsers = (Users) response;
                    m_cUser = lUsers;
                    switch ((Integer) refObj){
                        case CALL_PROCESS_PICTURE:
                            break;
                        case CALL_FIRST_NAME:
                            break;
                        case CALL_LAST_NAME:
                            break;
                    }
                    hideDialog();
                }else {
                    super.onAPIResponse(response, apiMethod, refObj);
                    hideDialog();
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error, String apiMethod, Object refObj) {
        switch (apiMethod) {
            default:
                if (apiMethod.contains(Constants.USERS)){
                    hideDialog();
                    if (error instanceof NoConnectionError) {
                        Toast.makeText(this, "Please check Network connection", Toast.LENGTH_SHORT).show();
                    } else {
                        String lMsg = new String(error.networkResponse.data);
                        showErrorMsg(lMsg);
                    }
                }else {
                    super.onErrorResponse(error, apiMethod, refObj);
                    hideDialog();
                }
                break;
        }
    }

    private void showPhotoOption(int pId, boolean pShow) {
        if (pShow)
            displaySpinnerDialog(pId, getResources().getString(R.string.add_photo_txt),
                    Arrays.asList(getResources().getString(R.string.go_to_camera), getResources().getString(R.string.go_to_gallery),
                            getResources().getString(R.string.delete_photo), getResources().getString(R.string.view_photo)), null);
        else
            displaySpinnerDialog(pId, getResources().getString(R.string.add_photo_txt),
                    Arrays.asList(getResources().getString(R.string.go_to_camera), getResources().getString(R.string.go_to_gallery)), null);

    }

    private void performPhotoOptions(int pId, String pLessonImgTag, ImageView mLessonImg) {
        switch (pId){
            case 0:
                takePhoto();
                break;
            case 1:
                showGallery();
                break;
            case 2:
                mLessonImg.setImageResource(R.drawable.profile_placeholder);
                break;
            case 3:
                showBottomSheet(mLessonImg);
                break;
        }
    }

    private void showBottomSheet(ImageView pLessonImgTag) {
        m_cSlider = BottomSheetBehavior.from(viewStub);
        m_cSlider.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {
            Picasso.with(EditProfileScreen.this)
                    .load(m_cUser.getProfilePic())
                    .error(R.drawable.profile_placeholder)
                    .placeholder(R.drawable.profile_placeholder)
                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .into(bottomImg);
        }catch (Exception e){
            e.printStackTrace();
        }

        m_cSlider.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING)
                    m_cSlider.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        viewStub.setVisibility(View.VISIBLE);
    }

    public void showGallery() {
        verifyStoragePermissions(this);
        m_cImageGUID = PotMacros.getGUID();
        File lFile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");
        Uri imageUri = Uri.fromFile(lFile);

        // Gallery.
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(galleryIntent, TAKE_PICTURE);
    }

    public void takePhoto() {
        m_cImageGUID = PotMacros.getGUID();
        File lFile = new File(PotMacros.getImageFilePath(this), m_cImageGUID + ".jpg");
        Uri imageUri = Uri.fromFile(lFile);

        // Camera.
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(captureIntent, TAKE_PICTURE);
    }

    class CaptureImage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            m_cImageProcessing = true;
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                File lFile = new File(PotMacros.getImageFilePath(EditProfileScreen.this), m_cImageGUID + ".jpg");
                if (lFile.exists()) {
                    //This inSampleSize option reduces memory consumption
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    m_cObjSelectedBitMap = BitmapFactory.decodeFile(lFile.getAbsolutePath(), options);

                    ExifInterface lObjExIf = new ExifInterface(lFile.getAbsolutePath());
                    //TODO addded for double cross check
                    int orientation = lObjExIf.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    Matrix matrix = new Matrix();
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                    }

                    //TODO : compress only camera images not the gallery
                    m_cObjSelectedBitMap = Bitmap.createBitmap(m_cObjSelectedBitMap, 0, 0,
                            m_cObjSelectedBitMap.getWidth(), m_cObjSelectedBitMap.getHeight(), matrix, true);

                    m_cObjSelectedBitMap = Utilities.scaleCameraImage(EditProfileScreen.this, m_cObjSelectedBitMap);

                    pTakePicture = true;
                    pSavePicture = false;

                    if (null != m_cObjSelectedBitMap) {
                        if (lFile.exists()) {
                            lFile.delete();
                        }
                        FileOutputStream outStream = new FileOutputStream(lFile.getAbsoluteFile());
                        m_cObjSelectedBitMap.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
                        outStream.flush();
                        outStream.close();
                        outStream = null;
                    }
                    //todo uncomment to save picture
//                    savePicture(lDate);

                    m_cObjSelectedBitMap = Utilities.scaleCameraImage(EditProfileScreen.this, m_cObjSelectedBitMap);

                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(e.getCause() + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideDialog();
            m_cImageProcessing = false;
            hideDialog();
            UserCircularImageView lview = null;
            try {
                switch (m_cCurrentImgId) {
                    case R.id.PROFILE_PIC_LL:
                        lview = m_cUserCivCell;
                        m_cUserCivCell.setTag(R.id.IMG_PATH, PotMacros.getImageFilePath(EditProfileScreen.this) + "/" + m_cImageGUID + ".jpg");
                        break;
                    default:
                        lview = m_cUserCivCell;
                        break;
                }
                Picasso.with(EditProfileScreen.this)
                        .load(new File(PotMacros.getImageFilePath(EditProfileScreen.this), m_cImageGUID + ".jpg"))
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .config(Bitmap.Config.RGB_565)
                        .fit()
                        .into(lview);
                m_cObjUIHandler.sendEmptyMessage(CALL_PROCESS_PICTURE);
            } catch (Exception e) {
                lview.setBackgroundResource(R.drawable.profile_placeholder);
                e.printStackTrace();
            }
        }
    }
}
