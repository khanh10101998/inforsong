package nhan1303.watsong.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import nhan1303.watsong.R;

public class NoResultActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView ivCancel;
    Button btnTryAgain;
    public static boolean tryAgain = false;
    public static boolean cancel = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_result);
        initControl();
        initEvent();
    }

    private void initEvent() {
        ivCancel.setOnClickListener(this);
        btnTryAgain.setOnClickListener(this);
    }

    private void initControl() {
        ivCancel = findViewById(R.id.ivCancel);
        btnTryAgain = findViewById(R.id.btnTryAgain);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivCancel:
                finish();
                cancel = true;
                break;
            case R.id.btnTryAgain:
                tryAgain = true;
                finish();
                break;
        }
    }
}
