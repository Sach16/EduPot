package com.cosmicdew.lessonpot.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cosmicdew.lessonpot.R;
import com.cosmicdew.lessonpot.baseclasses.PotFragmentBaseClass;
import com.cosmicdew.lessonpot.customviews.TouchImageView;
import com.cosmicdew.lessonpot.macros.PotMacros;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by S.K. Pissay on 14/11/16.
 */

public class ScreenSlidePageFragment extends PotFragmentBaseClass{

    TouchImageView imageZom;
    int m_cPos;
    String m_cKey;

    public static ScreenSlidePageFragment newInstance(int pPosition, String pUrl) {
        ScreenSlidePageFragment lScreenSlidePageFragment = new ScreenSlidePageFragment();

        Bundle args = new Bundle();
        args.putInt("Position", pPosition);
        args.putString("URL", pUrl);
        lScreenSlidePageFragment.setArguments(args);

        return lScreenSlidePageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup lView = (ViewGroup) inflater.inflate(
                R.layout.image_pager, container, false);
        imageZom = (TouchImageView) lView.findViewById(R.id.IMG_ZOOM);

        m_cPos = getArguments().getInt("Position", 0);
        m_cKey = getArguments().getString("URL");

        if (m_cKey.contains(PotMacros.HTTP_PREFIX)){
            try {
                Picasso.with(m_cObjMainActivity)
                        .load(m_cKey)
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .config(Bitmap.Config.RGB_565)
                        .fit()
                        .into(imageZom);
            } catch (Exception e){
                e.printStackTrace();
            }
        }else {
            try {
                Picasso.with(m_cObjMainActivity)
                        .load(new File(PotMacros.getImageFilePath(m_cObjMainActivity), m_cKey))
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .config(Bitmap.Config.RGB_565)
                        .fit()
                        .into(imageZom);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return lView;
    }

    @Override
    protected void handleUIMessage(Message pObjMessage) {

    }
}
