package nhan1303.watsong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

import nhan1303.watsong.R;
import nhan1303.watsong.activity.InfoSongActivity;
import nhan1303.watsong.activity.MainActivity;
import nhan1303.watsong.adapter.AdapterHistory;
import nhan1303.watsong.interfaceWatSong.CommunicationInterface;
import nhan1303.watsong.model.InfoSong;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by NHAN on 13/11/2017.
 */

@SuppressLint("ValidFragment")
public class FragmentHistory extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    Context context;
    private ListView lvHistory;
    ArrayList<InfoSong> listSongSaved;
    AdapterHistory adapterHistory;
    CommunicationInterface listener;

    CallbackManager callbackManager;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    MainActivity mainActivity;
    LoginButton btnLoginFacebook;
    InfoSong infoSong;

    SignInButton btnLoginGoogle;
    private FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    GoogleSignInClient mGoogleSignInClient;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final int RC_SIGN_IN = 998;
    private static final String TAG = "FRAGMENT_HISTORY";
    @SuppressLint("ValidFragment")
    public FragmentHistory(Context context) {
        this.context = context;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        initData();
        initDisplay();
        initEvent();
    }


    private void initFacebookTracker(){

        FacebookSdk.sdkInitialize(context);
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
        System.out.println("khanh ne AccessToken initFace:" + accessToken);

    }
    private void initGoogleTracker() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

       mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(getActivity(),
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                .addApi(Auth.GOOGLE_SIGN_IN_API, options).build();

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("khanh ne", "onResume fragment");
    }

    private void initData() {
        mAuth = FirebaseAuth.getInstance();
        mainActivity = new MainActivity();
        System.out.println("khanh ne calbackfragment: " + callbackManager);
        getFont();
        listSongSaved = new ArrayList<>();
        adapterHistory = new AdapterHistory(context, R.layout.item_history_list_view, listSongSaved);


    }

    private void initControl(View view) {
        lvHistory = view.findViewById(R.id.lvHistory);
        btnLoginFacebook =  view.findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setFragment(this);
        btnLoginGoogle = view.findViewById(R.id.btnLoginGoogle);
        btnLoginGoogle.setSize(SignInButton.SIZE_STANDARD);
    }

    private void initDisplay() {
        adapterHistory.notifyDataSetChanged();
        lvHistory.setAdapter(adapterHistory);
    }

    private void initEvent() {
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLoginFacebook.setReadPermissions("email", "public_profile");
                btnLoginFacebook.registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {

                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                handleFacebookAccessToken(loginResult.getAccessToken());
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(context, "on cancel", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Toast.makeText(context, "dang nhap fall", Toast.LENGTH_SHORT).show();
                                System.out.println("dang nhap fall");
                            }
                        });
            }
        });
        
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(context, InfoSongActivity.class);
                intent.putExtra("SELECTED_SONG", listSongSaved.get(position));
                startActivity(intent);
            }
        });

        lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + listSongSaved.get(position).getTitleTrack());
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.updateListSongAfterRemove(listSongSaved.get(positionToRemove));
                        listSongSaved.remove(positionToRemove);
                        adapterHistory.notifyDataSetChanged();
                    }});
                adb.show();
                return true;
            }
        });
    }

    public void updateFragment(ArrayList<InfoSong> infoSongs) {
        listSongSaved = infoSongs;
        adapterHistory = new AdapterHistory(context, R.layout.item_history_list_view, listSongSaved);
        lvHistory.setAdapter(adapterHistory);
    }

    private void getFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initFacebookTracker();
        initGoogleTracker();
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initControl(view);
        return view;
    }
    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleFacebookAccessToken(AccessToken token) {
        Log.d("MAIN_ACTIVITY", "Token_Facebook:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Log.d("MAIN_ACTIVITY", "credential: "+credential);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MAIN_ACTIVITY", "signInWithCredential:success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MAIN_ACTIVITY", "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        Log.d(TAG, "firebase_token:" + acct.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

//         Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if (result.isSuccess()) {
//                // Google Sign In was successful, authenticate with Firebas}
//                    GoogleSignInAccount account = result.getSignInAccount();
//                    firebaseAuthWithGoogle(account);
//
//            } else {
//                Toast.makeText(context, "results isn't success", Toast.LENGTH_SHORT).show();
//            }

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("FRAGMENT_HISTORY", "Google sign in failed", e);
            }
        }else {
            Toast.makeText(context, "Request code error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuth != null){
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Log.d("MAIN_ACTIVITY","onAuthStateChanged:sign_in" + currentUser.getUid());
            Toast.makeText(context, "Da login facebook", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Chua login facebook", Toast.LENGTH_SHORT).show();
            Log.d("MAIN_ACTIVITY","onAuthStateChanged:sign_out");
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed: "+connectionResult);
        Toast.makeText(context, "Google Play Serviec error", Toast.LENGTH_SHORT).show();
    }
}
