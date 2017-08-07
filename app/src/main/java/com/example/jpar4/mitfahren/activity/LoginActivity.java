package com.example.jpar4.mitfahren.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;


public class LoginActivity extends AppCompatActivity {
    TextView tv_logo;
    EditText et_input_id;
    EditText et_input_pw;
    Button btn_login;
    Button btn_join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_logo = (TextView) findViewById(R.id.tv_logo);
        et_input_id = (EditText) findViewById(R.id.et_input_id);
        et_input_pw = (EditText) findViewById(R.id.et_input_pw);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_join = (Button) findViewById(R.id.btn_join);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,  MainActivity.class);
                startActivity(intent);
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,  JoinActivity.class);
                startActivity(intent);
            }
        });
    }
}
