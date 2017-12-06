package nhan1303.watsong.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import nhan1303.watsong.R;
import nhan1303.watsong.model.InfoSong;

public class InfoSongActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnTouchListener {
    InfoSong infoSong;
    YouTubePlayerView youTubePlayerview;
    TextView tvSong, tvSinger, tvTitleSong, tvLyrics;
    public final static String API_KEY = "AIzaSyBZhAQWwdlRsAw74IyRWKAXkK6OF5-RQ8o";
    String idYouTube = "";
    String linkYoutube = "";
    String song, singer, lyrics;
    int requestVideo = 123;
    ImageView ivShareGoogle, ivShareFacebook, ivShareSms, imgCoverArt, ivBack;
    ShareDialog shareDialog;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    LinearLayout lineLyrics, linear_layout_share;
    RelativeLayout infoSingerLayout;
    CoordinatorLayout main_content;
    android.support.v7.widget.Toolbar toolbar;
    CallbackManager callbackManager;
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBarLayout;
    AppBarLayout.OnOffsetChangedListener mListener;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG_SIGN_IN_GOOGLE = "TAG_SIGN_IN_GOOGLE";
    Palette.Swatch textSwatch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebookTracker();
        initGoogle();
        setContentView(R.layout.activity_info_song);
        initData();
        initControl();
        initDisplay();
        initEvent();

    }

    private void initGoogle() {
        mGoogleApiClient = new GoogleApiClient.Builder(InfoSongActivity.this)
                .addConnectionCallbacks(InfoSongActivity.this)
                .addOnConnectionFailedListener(InfoSongActivity.this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    private void initData() {
        shareDialog = new ShareDialog(InfoSongActivity.this);
        lyrics = "Trái tim của em rất đau\nChỉ muốn buông tình ta ở đây\nVì cho đến giờ chẳng có ai biết em tồn tại\nNhững lần chào nhau bối rối\n Người ở bên cạnh anh chẳng nghi ngờ\n Lòng em lại chẳng nhẹ nhàng\n Chorus:\n Lời biệt ly buồn đến mấy cũng không thể nào làm cho em gục ngã đến mức tuyệt vọng\n Chỉ là vết thương sâu một chút thôi anh àh\n Ngày mà anh tìm đến, em tin anh thật lòng\n Và yêu em bằng những cảm xúc tự nguyện\n Làm em quá yêu nên mù quáng đến yếu lòng\n Là ngày chúng ta bắt đầu những sai lầm\n" +
                "Trái tim của em rất đau\nChỉ muốn buông tình ta ở đây\nVì cho đến giờ chẳng có ai biết em tồn tại\nNhững lần chào nhau bối rối\n Người ở bên cạnh anh chẳng nghi ngờ\n Lòng em lại chẳng nhẹ nhàng\n Chorus:\n Lời biệt ly buồn đến mấy cũng không thể nào làm cho em gục ngã đến mức tuyệt vọng\n Chỉ là vết thương sâu một chút thôi anh àh\n Ngày mà anh tìm đến, em tin anh thật lòng\n Và yêu em bằng những cảm xúc tự nguyện\n Làm em quá yêu nên mù quáng đến yếu lòng\n Là ngày chúng ta bắt đầu những sai lầm\n ";
        Intent intent = getIntent();
        if (intent.hasExtra("NEW_SONG")) {
            infoSong = (InfoSong) intent.getSerializableExtra("NEW_SONG");
        }

        if (intent.hasExtra("SELECTED_SONG")) {
            infoSong = (InfoSong) intent.getSerializableExtra("SELECTED_SONG");
        }

        song = infoSong.getTitleTrack();
        singer = infoSong.getArtistName();
        idYouTube = infoSong.getIdYoutube();
        linkYoutube = "https://www.youtube.com/watch?v=" + idYouTube;
    }

    private void initControl() {
        tvTitleSong = findViewById(R.id.tv_title_song);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        infoSingerLayout = findViewById(R.id.info_singer_layout);
        main_content = findViewById(R.id.main_content);
        lineLyrics = findViewById(R.id.line_lyrics);
        tvLyrics = findViewById(R.id.tv_lyrics);
        tvSong = findViewById(R.id.tvSong);
        tvSinger = findViewById(R.id.tv_singer);
        ivShareGoogle = findViewById(R.id.share_google_iv);
        ivShareFacebook = findViewById(R.id.share_facebook_iv);
        ivShareSms = findViewById(R.id.share_sms_iv);
        linear_layout_share = findViewById(R.id.linear_layout_share);
        ivBack = findViewById(R.id.iv_back);
        imgCoverArt = findViewById(R.id.img_singer);
        youTubePlayerview = findViewById(R.id.youtube);
    }

    private void initDisplay() {
        transparentStatusBar();
        youTubePlayerview.initialize(API_KEY, this);
        tvLyrics.setText(lyrics);
        tvSinger.setText(singer);
        tvTitleSong.setText(song);
        tvSong.setText(song);
        setImgCoverArt();
    }

    private void initEvent() {
        ivBack.setOnClickListener(v -> finish());
        ivShareFacebook.setOnTouchListener(this);
        ivShareGoogle.setOnTouchListener(this);
        ivShareSms.setOnTouchListener(this);
        linear_layout_share.setOnClickListener(v ->share(linkYoutube));
        mListener = ((AppBarLayout appBarLayout, int verticalOffset) -> setAnimToolBar(verticalOffset));
        appBarLayout.addOnOffsetChangedListener(mListener);

    }

    private void shareSms(String linkYoutube) {
        String content = "I used WatSong to discover \"" +
                song + "-" +
                singer + "\". \nLet's listen together ;)\n" +
                linkYoutube;

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", content);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    private void share(String linkYoutube) {
        String content = "I used WatSong to discover \"" +
                song + "-" +
                singer + "\". Let's listen together ;)\n" +
                linkYoutube;
        Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
        txtIntent .setType("text/plain");
        txtIntent .putExtra(android.content.Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(txtIntent ,"Share"));
    }


    private void shareGoogle() {
        Intent shareIntent = new PlusShare.Builder(this)
                .setType("text/plain")
                .setText("I used WatSong to discover \"" +
                        song + "-" +
                        singer + "\". Let's listen together ;)\n" +
                        linkYoutube)
                .setContentUrl(Uri.parse(linkYoutube))
                .getIntent();
        startActivityForResult(shareIntent, 0);
    }

    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void setImgCoverArt() {
        if (infoSong.getUrlImage().equals("")) {
            Picasso.with(this)
                    .load(infoSong.getIcPlaceHolder())
                    .into(imgCoverArt);
        } else {
            int imageDimension =
                    (int) getResources().getDimension(R.dimen.width_info_singer_iv);

            Picasso.with(this)
                    .load(infoSong.getUrlImage())
                    .resize(imageDimension, imageDimension)
                    .centerCrop()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            assert imgCoverArt != null;
                            imgCoverArt.setImageBitmap(bitmap);
                            Palette.from(bitmap)
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            textSwatch = palette.getVibrantSwatch();
                                            if (textSwatch == null) {
                                                Toast.makeText(InfoSongActivity.this, "Null swatch :(", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            collapsingToolbar.setBackgroundColor(textSwatch.getRgb());
                                            tvSong.setTextColor(textSwatch.getTitleTextColor());
                                            tvSinger.setTextColor(textSwatch.getBodyTextColor());
                                        }
                                    });
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

        }
    }

    private void setAnimToolBar(int verticalOffset) {
        if (collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
            //closed
            tvTitleSong.animate().alpha(1).setDuration(600);
            tvTitleSong.setVisibility(View.VISIBLE);
            infoSingerLayout.setVisibility(View.GONE);
        } else {
            tvTitleSong.animate().alpha(0).setDuration(600);
            tvTitleSong.setVisibility(View.GONE);
            infoSingerLayout.setVisibility(View.VISIBLE);
        }
    }

    private void shareFacebook() {
        if (shareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(linkYoutube))
                    .build();

            if (isAppInstalled("com.facebook.katana")) {
                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            } else {
                shareDialog.show(content, ShareDialog.Mode.WEB);
            }

        }
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void initFacebookTracker() {
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
            }
        };
        accessToken = AccessToken.getCurrentAccessToken();

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.cueVideo(idYouTube);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(InfoSongActivity.this, requestVideo);
        } else {
            Toast.makeText(this, "err", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestVideo) {
            youTubePlayerview.initialize(API_KEY, InfoSongActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG_SIGN_IN_GOOGLE, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()){
            case R.id.share_facebook_iv:
                if (action == MotionEvent.ACTION_DOWN) {
                    ivShareFacebook.setAlpha(100);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    ivShareFacebook.setAlpha(1000);
                    shareFacebook();
                    return true;
                }else{
                    return false;
                }


            case R.id.share_google_iv:
                if (action == MotionEvent.ACTION_DOWN) {
                    ivShareGoogle.setAlpha(100);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    ivShareGoogle.setAlpha(1000);
                    shareGoogle();
                    return true;
                }else{
                    return false;
                }

            case R.id.share_sms_iv:
                if (action == MotionEvent.ACTION_DOWN) {
                    ivShareSms.setAlpha(100);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    ivShareSms.setAlpha(1000);
                    shareSms(linkYoutube);
                    return true;
                }else{
                    return false;
                }

        }
        return false;
    }

}