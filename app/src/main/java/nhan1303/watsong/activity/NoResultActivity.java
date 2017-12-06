package nhan1303.watsong.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import nhan1303.watsong.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NoResultActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView ivCancel;
    Button btnTryAgain;
    public static boolean tryAgain = false;
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
                break;
            case R.id.btnTryAgain:
                tryAgain = true;
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
