package nhan1303.watsong.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import nhan1303.watsong.R;
import nhan1303.watsong.model.InfoSong;

public class InfoSongActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    InfoSong infoSong;
    YouTubePlayerView youTubePlayerview;
    TextView tvSong, tvSinger, nameSongActivityInformationSong, tvLyrics;
    String apiKey, idYouTube, song, singer, lyrics;
    int requestVideo = 123;
    ShareButton btnFacebookShare;
    ImageView ivShareGoogle, ivShareFacebook, imgCoverArt, ivBack;
    ShareDialog shareDialog;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    LinearLayout lineLyrics;
    android.support.v7.widget.Toolbar toolbar;
    CallbackManager callbackManager;
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBarLayout;
    AppBarLayout.OnOffsetChangedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebookTracker();
        setContentView(R.layout.activity_information_song);
        initData();
        initControl();
        initDisplay();
        initEvent();
    }

    private void initData() {
        Intent intent = getIntent();
        infoSong = (InfoSong) intent.getSerializableExtra("SONG");
        shareDialog = new ShareDialog(InfoSongActivity.this);
        song = infoSong.getTitle_track();
        singer = infoSong.getArtist_name();
        lyrics = "Trái tim của em rất đau\nChỉ muốn buông tình ta ở đây\nVì cho đến giờ chẳng có ai biết em tồn tại\nNhững lần chào nhau bối rối\n Người ở bên cạnh anh chẳng nghi ngờ\n Lòng em lại chẳng nhẹ nhàng\n Chorus:\n Lời biệt ly buồn đến mấy cũng không thể nào làm cho em gục ngã đến mức tuyệt vọng\n Chỉ là vết thương sâu một chút thôi anh àh\n Ngày mà anh tìm đến, em tin anh thật lòng\n Và yêu em bằng những cảm xúc tự nguyện\n Làm em quá yêu nên mù quáng đến yếu lòng\n Là ngày chúng ta bắt đầu những sai lầm\n ";
        apiKey = "AIzaSyCMkVqZIRStpWo9unP9D9GxesjCTOrEPNE";
        idYouTube = infoSong.getId_youtube();
    }

    private void initControl() {
        nameSongActivityInformationSong = findViewById(R.id.name_song_activity_information_song);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        lineLyrics = findViewById(R.id.line_lyrics);
        tvLyrics = findViewById(R.id.tv_lyrics);
        tvSong = findViewById(R.id.tvSong);
        tvSinger = findViewById(R.id.tv_singer);
        ivShareGoogle = findViewById(R.id.share_google_iv);
        ivShareFacebook = findViewById(R.id.share_facebook_iv);
        ivBack = findViewById(R.id.iv_back);
        imgCoverArt = findViewById(R.id.img_singer);
        btnFacebookShare = findViewById(R.id.fb_share_button);
        youTubePlayerview = findViewById(R.id.youtube);
        youTubePlayerview.initialize(apiKey, this);
    }

    private void initDisplay() {
        tvLyrics.setText(lyrics);
        nameSongActivityInformationSong.setText(song);
        tvSinger.setText(singer);
        tvSong.setText(song);
        setImgCoverArt();


    }

    private void initEvent() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnFacebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareYoutube(idYouTube);
            }
        });


        ivShareGoogle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    ivShareGoogle.setAlpha(100);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    ivShareGoogle.setAlpha(1000);
                    return true;
                } else {
                    return false;
                }
            }
        });

        ivShareFacebook.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    ivShareFacebook.setAlpha(100);
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    ivShareFacebook.setAlpha(1000);
                    btnFacebookShare.performClick();
                    return true;
                } else {
                    return false;
                }
            }
        });

        mListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                setAnimToolBar(verticalOffset);
            }
        };
        appBarLayout.addOnOffsetChangedListener(mListener);
    }

    private void setImgCoverArt() {
        if (infoSong.getUrlImage().equals("")) {
            Picasso.with(this)
                    .load(infoSong.getIc_place_holder())
                    .into(imgCoverArt);
        } else {
            Picasso.with(this)
                    .load(infoSong.getUrlImage())
                    .placeholder(R.drawable.ic_place_holder)
                    .into(imgCoverArt);
        }
    }

    private void setAnimToolBar(int verticalOffset) {
        if (collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
            toolbar.setBackgroundResource(R.drawable.background1);
            nameSongActivityInformationSong.animate().alpha(1).setDuration(600);

        } else {
            nameSongActivityInformationSong.animate().alpha(0).setDuration(600);
            toolbar.setBackgroundResource(R.color.transparent);
        }
    }

    private void shareYoutube(String idYouTube) {
        if (shareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://www.youtube.com/watch?v=" + idYouTube))
                    .build();
            shareDialog.show(content);
        }
    }


    private void initFacebookTracker() {

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
            youTubePlayerview.initialize(apiKey, InfoSongActivity.this);
        }
    }
}