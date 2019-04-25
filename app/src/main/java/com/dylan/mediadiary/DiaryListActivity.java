package com.dylan.mediadiary;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DiaryListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DiaryListFragment();
    }
}
