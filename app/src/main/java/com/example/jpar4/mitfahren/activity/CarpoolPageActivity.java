package com.example.jpar4.mitfahren.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.jpar4.mitfahren.R;

public class CarpoolPageActivity extends AppCompatActivity {
    Button btn_chatting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_page);

        Button btn_chatting = (Button)findViewById(R.id.btn_chatting);
        btn_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarpoolPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
