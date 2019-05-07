package com.example.gp62.studymate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MemberManageActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar select_member_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_member);

        select_member_toolbar = findViewById(R.id.select_member_toolbar);

        // 툴바에 뒤로가기버튼 세팅
        setSupportActionBar(select_member_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // 뒤로가기 버튼이 눌러졌을 때, 현재 화면이 닫힌다.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
