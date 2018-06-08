package com.retrofit.androidsqlliteutils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.retrofit.androidsqlliteutils.bean.Student;
import com.retrofit.androidsqlliteutils.db.BaseDao;
import com.retrofit.androidsqlliteutils.db.BaseDaoFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected TextView vtInsertData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.vt_insertData) {

            BaseDao baseDao = BaseDaoFactory.getOurInstance().getBaseDao(Student.class);
            baseDao.insert(new Student("18", "男", "1.8米"));
        }
    }

    private void initView() {
        vtInsertData = (TextView) findViewById(R.id.vt_insertData);
        vtInsertData.setOnClickListener(MainActivity.this);
        Toast.makeText(this,"添加成功",Toast.LENGTH_LONG).show();
    }
}
