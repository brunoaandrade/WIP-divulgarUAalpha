package authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import deti.ua.main.ActivateAccountActivity;
import deti.ua.main.R;

public class OauthActivity extends Activity {

    private WebView mWebView;
    private OAuthService mService;
    private Token mRequestToken;
    private static  int btnSendCodeCount = 0;



    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_oauth);

        Button button= (Button) findViewById(R.id.btn_confirm);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText txtUserName = (EditText) findViewById(R.id.txtPIN);
                String strUserName = txtUserName.getText().toString();


                Log.i("CENAS", strUserName);
                if (!(TextUtils.isEmpty(strUserName))) {
                    final Verifier verifier = new Verifier(strUserName);
                    Log.i("CENAS", verifier.getValue());
                    (new AsyncTask<Void, Void, Token>() {
                        @Override
                        protected Token doInBackground(Void... params) {
                            return mService.getAccessToken(mRequestToken, verifier);
                        }

                        @Override
                        protected void onPostExecute(Token accessToken) {
                            Log.i("CENAS", accessToken.getToken());
                            Log.i("CENAS", accessToken.getSecret());
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("ua_access_token", accessToken.getToken());
                            editor.putString("ua_access_secret", accessToken.getSecret());
                            //editor.putBoolean("isLogin", true);
                            editor.commit();
                            Intent intent = new Intent(OauthActivity.this, ActivateAccountActivity.class);
                            startActivity(intent);
                            //finish();
                        }
                    }).execute();
                }
            }
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mService = new ServiceBuilder()
                .provider(IdentityUAApi.class)
                .apiKey(IdentityUAApi.getConsumerKey())
                .apiSecret(IdentityUAApi.getConsumerSecret())
        //.callback("x-oauthflow://callback")
                        .build();

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient());

       // setContentView(mWebView);

        startAuthorize();
    }

    private void startAuthorize() {
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                mRequestToken = mService.getRequestToken();
                Log.i("CENAS",mRequestToken.getRawResponse());
                return  mService.getAuthorizationUrl(mRequestToken);

            }

            @Override
            protected void onPostExecute(String url) {
                Log.i("CENAS",url);
                mWebView.loadUrl(url);
            }
        }).execute();
    }



    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if ((url != null) ) { // Override webview when user came back to CALLBACK_URL
                //mWebView.stopLoading();
                //mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                //Uri uri = Uri.parse(url);
                if(url.contains("consent=y")) {
                    FrameLayout frame = (FrameLayout) findViewById(R.id.frame_pin);
                    frame.setVisibility(View.VISIBLE);

                }

            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };


    @Override
    public void onBackPressed() {
    }
}
