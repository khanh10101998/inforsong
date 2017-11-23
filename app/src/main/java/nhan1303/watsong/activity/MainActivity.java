package nhan1303.watsong.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import nhan1303.watsong.R;
import nhan1303.watsong.adapter.AdapterViewPager;
import nhan1303.watsong.fragment.FragmentHistory;
import nhan1303.watsong.fragment.FragmentMain;
import nhan1303.watsong.interfaceWatSong.CommunicationInterface;
import nhan1303.watsong.model.InfoSong;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, CommunicationInterface {
    public static final String TAG = "MainActivity";
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Context context;
    public ViewPager viewPager;
    public AdapterViewPager adapterViewPager;
    private Button btn_fragment_history, btn_fragment_main;
    private LinearLayout myWatSongLayout;
    FragmentHistory fragmentHistory;
    FragmentMain fragmentMain;
    ArrayList<InfoSong> infoSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initControl();
        initDisplay();
        initEvent();


    }

    private void initData() {
        checkingRecordPermissions();
        context = getApplicationContext();

        infoSongs = new ArrayList<>();

        fragmentMain = new FragmentMain(context);
        fragmentHistory = new FragmentHistory(context);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragmentHistory);
        fragmentList.add(fragmentMain);

        adapterViewPager = new AdapterViewPager(context, getSupportFragmentManager(), fragmentList);
    }

    private void initControl() {
        btn_fragment_history = findViewById(R.id.btn_fragment_history);
        btn_fragment_main = findViewById(R.id.btn_fragment_main);
        viewPager = findViewById(R.id.viewPager);
        myWatSongLayout = findViewById(R.id.myWatSongLayout);
    }

    private void initDisplay() {
        transparentStatusBar();
        viewPager.setAdapter(adapterViewPager);
        viewPager.setCurrentItem(1);
        adapterViewPager.notifyDataSetChanged();
    }

    private void initEvent() {
        btn_fragment_history.setOnClickListener(this);
        btn_fragment_main.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
        myWatSongLayout.setOnClickListener(this);
    }

    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void checkingRecordPermissions() {
        if (permissionToRecordAccepted == false) {
            Log.i(TAG, "Checking permissions.");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Contacts permissions have not been granted.
                Log.i(TAG, "RECORD_AUDIO permissions has NOT been granted. Requesting permissions.");
                ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
                permissionToRecordAccepted = true;
            } else {
                Log.i(TAG,
                        "RECORD_AUDIO permissions have already been granted.");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fragment_history:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_fragment_main:
                viewPager.setCurrentItem(1);
                break;
            case R.id.myWatSongLayout:
                viewPager.setCurrentItem(0);
                myWatSongLayout.setVisibility(View.GONE);
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        btn_fragment_history.setBackgroundResource(R.drawable.circle_default);
        btn_fragment_main.setBackgroundResource(R.drawable.circle_default);
        switch (position) {
            case 0:
                btn_fragment_history.setBackgroundResource(R.drawable.circle_on);
                myWatSongLayout.setVisibility(View.GONE);
                break;
            case 1:
                btn_fragment_main.setBackgroundResource(R.drawable.circle_on);
                myWatSongLayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClickFragmentMain(InfoSong infoSong) {
        infoSongs.add(infoSong);
        if (fragmentHistory != null) { // kiểm tra Fragment cần truyền data đến có thực sự tồn tại và đang hiện.
            fragmentHistory.updateFragment(infoSongs);
            Log.d("DATA_FRAGMENT", "UPDATE_FRAGMENT: " + "UPDATE OK");
        } else {
            Log.d("DATA_FRAGMENT", "UPDATE_FRAGMENT: " + "UPDATE FAIL");
        }
    }
}
