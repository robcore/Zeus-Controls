/*
 * Copyright (C) 2015 Willi Ye
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swapnil133609.zeuscontrols.fragments.kernel;

import android.os.Bundle;

import com.swapnil133609.zeuscontrols.R;
import com.swapnil133609.zeuscontrols.elements.cards.CardViewItem;
import com.swapnil133609.zeuscontrols.elements.cards.SeekBarCardView;
import com.swapnil133609.zeuscontrols.fragments.RecyclerViewFragment;
import com.swapnil133609.zeuscontrols.utils.Constants;
import com.swapnil133609.zeuscontrols.utils.kernel.LMK;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 27.12.14.
 */
public class LMKFragment extends RecyclerViewFragment implements Constants {

    private SeekBarCardView.DSeekBarCard[] mMinFreeCard;
    private CardViewItem.DCardView[] mProfileCard;

    private final List<String> values = new ArrayList<>(), modifiedvalues = new ArrayList<>();

    private final String[] mProfileValues = new String[]{
            "512,1024,1280,2048,3072,4096", "1024,2048,2560,4096,6144,8192", "1024,2048,4096,8192,12288,16384",
            "2048,4096,8192,16384,24576,32768", "4096,8192,16384,32768,49152,65536"};

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        values.clear();
        modifiedvalues.clear();
        for (int x = 0; x < 257; x++) {
            modifiedvalues.add(x + getString(R.string.mb));
            values.add(String.valueOf(x * 256));
        }

        List<String> minfrees = LMK.getMinFrees();
        mMinFreeCard = new SeekBarCardView.DSeekBarCard[minfrees.size()];
        try {
            for (int i = 0; i < minfrees.size(); i++) {
                mMinFreeCard[i] = new SeekBarCardView.DSeekBarCard(modifiedvalues);
                mMinFreeCard[i].setTitle(getResources().getStringArray(R.array.lmk_names)[i]);
                mMinFreeCard[i].setProgress(modifiedvalues.indexOf(LMK.getMinFree(minfrees, i) / 256 + getString(R.string.mb)));
                mMinFreeCard[i].setOnDSeekBarCardListener(new SeekBarCardView.DSeekBarCard.OnDSeekBarCardListener() {
                    @Override
                    public void onChanged(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
                    }

                    @Override
                    public void onStop(SeekBarCardView.DSeekBarCard dSeekBarCard, int position) {
                        List<String> minFrees = LMK.getMinFrees();
                        String minFree = "";

                        for (int i = 0; i < mMinFreeCard.length; i++)
                            if (dSeekBarCard == mMinFreeCard[i])
                                minFree += minFree.isEmpty() ? values.get(position) : "," + values.get(position);
                            else
                                minFree += minFree.isEmpty() ? minFrees.get(i) : "," + minFrees.get(i);

                        LMK.setMinFree(minFree, getActivity());
                        refresh();
                    }
                });

                addView(mMinFreeCard[i]);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        mProfileCard = new CardViewItem.DCardView[mProfileValues.length];
        for (int i = 0; i < mProfileValues.length; i++) {
            mProfileCard[i] = new CardViewItem.DCardView();
            mProfileCard[i].setTitle(getResources().getStringArray(R.array.lmk_profiles)[i]);
            mProfileCard[i].setDescription(mProfileValues[i]);
            mProfileCard[i].setOnDCardListener(new CardViewItem.DCardView.OnDCardListener() {
                @Override
                public void onClick(CardViewItem.DCardView dCardView) {
                    for (CardViewItem.DCardView profile : mProfileCard)
                        if (dCardView == profile) {
                            LMK.setMinFree(dCardView.getDescription().toString(), getActivity());
                            refresh();
                        }
                }
            });

            addView(mProfileCard[i]);
        }
    }

    private void refresh() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            List<String> minfrees = LMK.getMinFrees();
                            if (minfrees == null) return;
                            for (int i = 0; i < minfrees.size(); i++)
                                try {
                                    mMinFreeCard[i].setProgress(modifiedvalues.indexOf(LMK.getMinFree(minfrees, i) / 256
                                            + getString(R.string.mb)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

}
