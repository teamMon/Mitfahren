package com.example.jpar4.mitfahren.tmap_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;

public class tmap_first_a extends AppCompatActivity implements View.OnClickListener{
    Button btnClear;
    Button btnSend;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmap_first_activity);
        initUI();
    }

    public void initUI() {
        btnClear = (Button) findViewById(R.id.btnClear);
        btnSend = (Button) findViewById(R.id.btnSend);
        tvResult = (TextView) findViewById(R.id.tvResult);
        btnSend.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnClear: {
                tvResult.setText("");
                break;
            }

            case R.id.btnSend: {
                commWithOpenApiServer();
                break;
            }
        }
    }
    public void commWithOpenApiServer() {

    }
}
