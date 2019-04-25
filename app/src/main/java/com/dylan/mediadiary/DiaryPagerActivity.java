package com.dylan.mediadiary;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.UUID;

public class DiaryPagerActivity extends AppCompatActivity {
    private static final String EXTRA_DIARY_ID = "com.dylan.mediadiary.diary_id";
    private ViewPager viewPager;
    private List<Diary> diaries;

    public static Intent newIntent(Context packageContext, UUID diaryId) {
        Intent intent = new Intent(packageContext, DiaryPagerActivity.class);
        intent.putExtra(EXTRA_DIARY_ID, diaryId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_pager);
        UUID diaryId = (UUID) getIntent().getSerializableExtra(EXTRA_DIARY_ID);
        viewPager = (ViewPager) findViewById(R.id.diary_view_pager);
        diaries = DiaryLab.get(this).getDiaries();
        FragmentManager fm = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Diary diary = diaries.get(position);
                return DiaryFragment.newInstance(diary.getId());
            }

            @Override
            public int getCount() {
                return diaries.size();
            }
        });

        for (int i = 0; i < diaries.size(); i++) {
            if (diaries.get(i).getId().equals(diaryId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
