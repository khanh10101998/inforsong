package nhan1303.watsong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.IACRCloudListener;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.skyfishjy.library.RippleBackground;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import nhan1303.watsong.activity.InfoSongActivity;
import nhan1303.watsong.activity.NoResultActivity;
import nhan1303.watsong.animation.MyBounceInterpolator;
import nhan1303.watsong.R;
import nhan1303.watsong.interfaceWatSong.CommunicationInterface;
import nhan1303.watsong.model.InfoSong;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by NHAN on 13/11/2017.
 */

@SuppressLint("ValidFragment")
public class FragmentMain extends Fragment implements IACRCloudListener {
    private final static int REQUEST_CODE_WIRELESS_SETTINGS = 111;
    private Context context;
    private Button btnRecord;
    private ShimmerTextView tvTap;
    private TextView tvTapCancel;
    private Shimmer shimmer;
    private RippleBackground rippleBackground;
    private MyBounceInterpolator interpolator;
    private Animation myAnim;
    private ACRCloudClient mClient;
    private ACRCloudConfig mConfig;
    private boolean mProcessing = false;
    private boolean initState = false;
    private String title_track = "";
    private String artist_name = "";
    private String vid = "";
    String linkCoverArt = "";
    private boolean hasCoverArt = false;
    private boolean isTapped = false;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm, dd-MM-yyyy", Locale.getDefault());
    private CommunicationInterface listener;

    @SuppressLint("ValidFragment")
    public FragmentMain(Context context) {
        this.context = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initDisplay();
        initEvent();
    }

    public FragmentMain() {
    }

    private void initData() {
        initARCSound();
        initRippleAnimationButton();
        initBounceAnimmation();
    }

    private void initControl(View view) {
        btnRecord = view.findViewById(R.id.btnRecord);
        tvTap = view.findViewById(R.id.tvTap);
        tvTapCancel = view.findViewById(R.id.tvTapCancel);
    }

    private void initDisplay() {
        shimmerForTextView(tvTap);
        btnRecord.startAnimation(myAnim);
    }

    private void initEvent() {
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    if (isTapped == false) {
                        updateUIActive();
                        start();
                    } else {
                        cancel();
                        updateUINormal();
                    }
                } else {
                    showDialogInternet();
                }
            }
        });

    }

    private void updateUIActive() {
        isTapped = true;
        rippleBackground.startRippleAnimation();
        tvTapCancel.setText("Tap to cancel.");
        tvTap.setText("Listening...");
    }

    private void updateUINormal() {
        rippleBackground.stopRippleAnimation();
        tvTapCancel.setText("");
        tvTap.setText("Tap to identify music");
        isTapped = false;
    }

    private void showDialogInternet() {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
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

    private void initARCSound() {
        String path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud/model";

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;

        // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
        //this.mConfig.acrcloudResultWithAudioListener = this;

        this.mConfig.context = context;
        this.mConfig.host = "identify-ap-southeast-1.acrcloud.com";
        this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        this.mConfig.accessKey = "0d927e03f3c8448582d04bb55cee5218";
        this.mConfig.accessSecret = "hJ0IxhOE4m3gJBnKskTVbQnsICJrvrDnSWegi9WU";
        this.mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP; // PROTOCOL_HTTPS
        this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

        this.mClient = new ACRCloudClient();
        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
        // the function initWithConfig is used to load offline db, and it may cost long time.
        this.initState = this.mClient.initWithConfig(this.mConfig);
        if (this.initState) {
            this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }
    }

    private void initRippleAnimationButton() {
        rippleBackground = (RippleBackground) getView().findViewById(R.id.content);
    }

    private void initBounceAnimmation() {
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        myAnim.setInterpolator(interpolator);

    }

    private void shimmerForTextView(ShimmerTextView shimmerTextView) {
        shimmer = new Shimmer();
        shimmer.setStartDelay(100).setDuration(2200).setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        shimmer.start(shimmerTextView);
    }

    private boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        return connected;
    }

    private void createSong() {
        InfoSong infoSong;
        String currentTime = format.format(new Date());
        if (hasCoverArt) {
            infoSong = new InfoSong(title_track, artist_name, linkCoverArt, currentTime, vid);
            hasCoverArt = false;
            vid = "";
            linkCoverArt = "";
            Log.d("RESULT", "Create Song Object: " + infoSong.toString());
        } else {
            infoSong = new InfoSong(title_track, artist_name, R.drawable.ic_place_holder, currentTime, vid);
            vid = "";
        }

        listener.onClickFragmentMain(infoSong);

        Intent intent = new Intent(context, InfoSongActivity.class);
        intent.putExtra("NEW_SONG", infoSong);
        startActivity(intent);
    }

    public void start() {
        if (!this.initState) {
            Toast.makeText(context, "Init error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
            }
        }
    }

    protected void cancel() {
        if (mProcessing && this.mClient != null) {
            mProcessing = false;
            this.mClient.cancel();
        }
    }

    @Override
    public void onResult(String result) {
        if (this.mClient != null) {
            this.mClient.cancel();
            mProcessing = false;
        }

        String tres = "\n";

        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            if (j2 == 0) {
                JSONObject metadata = j.getJSONObject("metadata");

                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");
                    for (int i = 0; i < musics.length(); i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        title_track = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        artist_name = art.getString("name");
                        tres = tres + (i + 1) + ".  Title: " + title_track + "    Artist: " + artist_name + "\n";

                        JSONObject external_metadata = tt.getJSONObject("external_metadata");

                        if (external_metadata.has("youtube")) {
                            JSONObject youtube = external_metadata.getJSONObject("youtube");
                            vid = youtube.getString("vid");
                        } else {
                            new GetIDYoutubeAsyncTask().execute();
                        }

                        if (external_metadata.has("spotify")) {
                            JSONObject spotify = external_metadata.getJSONObject("spotify");
                            JSONObject track = spotify.getJSONObject("track");
                            String idTrack = track.getString("id");
                            hasCoverArt = true;
                            new GetCoverArtAsyncTask().execute(idTrack);
                            break;
                        }

                    }

                }

                tres = tres + "\n\n" + result;
            } else {
                Intent intent = new Intent(context, NoResultActivity.class);
                startActivity(intent);
                tres = result;
            }
        } catch (JSONException e) {
            tres = result;
            e.printStackTrace();
        }

        Log.d("RESULT", "JSON: " + tres);
        updateUINormal();
    }

    @Override
    public void onVolumeChanged(double v) {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initControl(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CommunicationInterface) {
            listener = (CommunicationInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "You Need To Implement CommunicationInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        btnRecord.startAnimation(myAnim);
        if (NoResultActivity.tryAgain) {
            btnRecord.callOnClick();
            NoResultActivity.tryAgain = false;
        }
    }

    class GetCoverArtAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = "https://open.spotify.com/oembed?url=spotify:track:" + params[0];
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response responses = null;

                try {
                    responses = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonData = responses.body().string();
                JSONObject Jobject = new JSONObject(jsonData);
                linkCoverArt = Jobject.getString("thumbnail_url");

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("RESULT", "LINK_COVER_ART: " + linkCoverArt);

            return linkCoverArt;
        }

        @Override
        protected void onPostExecute(String linkCoverArt) {
            super.onPostExecute(linkCoverArt);
            createSong();
        }
    }

    class GetIDYoutubeAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q=" + title_track + "-" + artist_name + "&key=AIzaSyDHd4sAjoUsi7Gme2g6dHHClZpTQsLyT4E";

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response responses = null;

                try {
                    responses = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String jsonData = responses.body().string();
                JSONObject Jobject = new JSONObject(jsonData);
                JSONArray items = Jobject.getJSONArray("items");
                JSONObject item = (JSONObject) items.get(0);
                JSONObject idObject = (JSONObject) item.get("id");
                vid = idObject.getString("videoId");
                Log.d("RESULT", "VID: " + vid);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!hasCoverArt){
                createSong();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
}
