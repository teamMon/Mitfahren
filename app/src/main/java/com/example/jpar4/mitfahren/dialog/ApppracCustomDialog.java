package com.example.jpar4.mitfahren.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;


public class ApppracCustomDialog extends Dialog {

    private TextView mTitleView;
    private TextView mContentView;
    private Button mLeftButton;
    private Button mRightButton;
    private String mTitle;
    private String mContent;
    private View.OnClickListener mLeftClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_main_explain);

        mTitleView = (TextView) findViewById(R.id.txt_title);
        mContentView = (TextView) findViewById(R.id.txt_content);
        mLeftButton = (Button) findViewById(R.id.btn_left);

        // 제목과 내용을 생성자에서 셋팅한다.
       /* mTitleView.setText(mTitle);
        mContentView.setText(mContent);*/

        mTitleView.setText("우리 카풀 해요!");
       /* mContentView.setText(
                "1부터 9의 숫자가 차례대로 \n" +
                "버튼에 나타납니다. 9까지 숫자가 모두\n" +
                "나타나면 모든 숫자가 사라집니다.\n" +
                "그럼 숫자가 나타난 순서대로\n" +
                "버튼을 누르시면 됩니다.\n" +
                        "버튼을 순서대로 누르면 번호가 나타나고\n" +
                        "실패시 게임이 종료됩니다."
        );*/
        mContentView.setText(
                "출발지와 도착지를 정해주세요.\n\n"
                +"경로에 맞는 운전자를 검색할 수 있습니다.\n"
        );
        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        }
    }
        // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
        public ApppracCustomDialog(Context context, String title,
                                   View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mLeftClickListener = singleListener;
       }
        // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
        public ApppracCustomDialog(Context context, String title,
                                   String content, View.OnClickListener leftListener) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.mTitle = title;
            this.mContent = content;
            this.mLeftClickListener = leftListener;

    }
}
