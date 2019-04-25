package com.dylan.mediadiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recog);
        getOverflowMenu();
    }

    //三个点的menu图标现在需要自己通过反射调用出来
    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);//得到一个已经设置好设备的显示密度的对象
            Field menuKeyField = ViewConfiguration.class .getDeclaredField("sHasPermanentMenuKey");//反射获取其中的方法sHasPermanentMenuKey()，他的作用是报告设备的菜单是否对用户可用，如果不可用可强制可视化。
            if (menuKeyField != null) { //强制设置参数,让其重绘三个点
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception e) { e.printStackTrace(); } }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_voice_diary:
                intent = new Intent(MainActivity.this, RecogActivity.class);
                startActivity(intent);
                break;
            case R.id.add_diary:
                intent = new Intent(MainActivity.this, DiaryActivity.class);
                startActivity(intent);
            default:
        }
        return true;
    }
}
