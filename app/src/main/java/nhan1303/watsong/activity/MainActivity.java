package nhan1303.watsong.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nhan1303.watsong.R;
import nhan1303.watsong.adapter.AdapterViewPager;
import nhan1303.watsong.fragment.FragmentHistory;
import nhan1303.watsong.fragment.FragmentMain;
import nhan1303.watsong.interfaceWatSong.CommunicationInterface;
import nhan1303.watsong.model.InfoSong;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, CommunicationInterface {
    public static final String TAG = "MainActivity";
    // Requesting permission to RECORD_AUDIO

    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final static int REQUEST_CODE_WIRELESS_SETTINGS = 111;
    private Context context;
    public ViewPager viewPager;
    public AdapterViewPager adapterViewPager;
    private ImageView iv_fragment_history, iv_fragment_main;
    private LinearLayout myWatSongLayout;
    FragmentHistory fragmentHistory;
    FragmentMain fragmentMain;
    ArrayList<InfoSong> infoSongs;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebookTracker();
        setContentView(R.layout.activity_main);
        initData();
        initControl();
        initDisplay();
        initEvent();

    }

    private void initData() {
        getFont();
        checkingRecordPermissions();
        context = getApplicationContext();

        infoSongs = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("listSong");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                infoSongs.clear();
                for (DataSnapshot songSnapshot: dataSnapshot.getChildren()) {
                    InfoSong song = songSnapshot.getValue(InfoSong.class);
                    infoSongs.add(song);
                    Log.d("DATA_FRAGMENT", "LOAD_FIREBASE: " + song.toString());
                }
                if (fragmentHistory != null) {
                    fragmentHistory.updateFragment(infoSongs);
                    Log.d("DATA_FRAGMENT", "LOAD_FIREBASE: " + "LOAD OK");
                } else {
                    Log.d("DATA_FRAGMENT", "LOAD_FIREBASE: " + "LOAD FAIL");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fragmentMain = new FragmentMain(context);
        fragmentHistory = new FragmentHistory(context);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragmentHistory);
        fragmentList.add(fragmentMain);

        adapterViewPager = new AdapterViewPager(context, getSupportFragmentManager(), fragmentList);
    }

    private void getFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void initControl() {
        iv_fragment_history = findViewById(R.id.iv_fragment_history);
        iv_fragment_main = findViewById(R.id.iv_fragment_main);
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
        iv_fragment_history.setOnClickListener(this);
        iv_fragment_main.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
        myWatSongLayout.setOnClickListener(this);

    }



    private boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        return connected;
    }
    private void showDialogInternet() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Oh no!");
        adb.setMessage("Your device isn't connected to the Internet.");
        adb.setPositiveButton("OK", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        adb.setNegativeButton("Setting", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), REQUEST_CODE_WIRELESS_SETTINGS);
                dialog.dismiss();
            }
        });
        adb.show();
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
            case R.id.iv_fragment_history:
                viewPager.setCurrentItem(0);
                break;
            case R.id.iv_fragment_main:
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
        iv_fragment_history.setImageDrawable(getResources().getDrawable(R.drawable.circle_default));
        iv_fragment_main.setImageDrawable(getResources().getDrawable(R.drawable.circle_default));
        switch (position) {
            case 0:

                if(!isNetworkAvailable()){
                    showDialogInternet();
                }
                iv_fragment_history.setImageDrawable(getResources().getDrawable(R.drawable.circle_on));
                myWatSongLayout.setVisibility(View.GONE);
                break;
            case 1:
                iv_fragment_main.setImageDrawable(getResources().getDrawable(R.drawable.circle_on));
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
        saveSongToDB(infoSong);
        if (fragmentHistory != null) {
            fragmentHistory.updateFragment(infoSongs);
            Log.d("DATA_FRAGMENT", "UPDATE_FRAGMENT: " + "UPDATE OK");
        } else {
            Log.d("DATA_FRAGMENT", "UPDATE_FRAGMENT: " + "UPDATE FAIL");
        }
    }

    private void saveSongToDB(InfoSong infoSong) {
        myRef.push().setValue(infoSong);
    }

    @Override
    public void updateListSongAfterRemove(InfoSong infoSong) {
        InfoSong songDelete = infoSong;
        deleteFromDB(songDelete);
    }

    private void deleteFromDB(InfoSong songDelete) {
        Query query = myRef.orderByChild("titleTrack").equalTo(songDelete.getTitleTrack());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot songSnapshot: dataSnapshot.getChildren()) {
                    songSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        System.out.println("khanh ne onactivityResult: "+ callbackManager);
        if(requestCode == REQUEST_CODE_WIRELESS_SETTINGS) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isNetworkAvailable()) {
                        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "No Internet Access", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 5000);
        }
    }

    private void initFacebookTracker(){

        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        accessToken = AccessToken.getCurrentAccessToken();

    }


}
