package kmt.jimmy.autoloadpage;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;

import java.util.Timer;
import java.util.TimerTask;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener  {

    TabHost host;
    WebView wbYoutube, wbDanTri, wb24H;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //region Get Controls
        host = (TabHost)findViewById(R.id.tabhost);
        wbYoutube = (WebView) findViewById(R.id.wbYoutube);
        wbDanTri = (WebView) findViewById(R.id.wbDanTri);
        wb24H = (WebView) findViewById(R.id.wb24H);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
        //endregion

        //region Setup Tab
        host.setup();
        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Youtube");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Tivi Online");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Dân Trí");
        host.addTab(spec);

        //Tab 4
        spec = host.newTabSpec("Tab Four");
        spec.setContent(R.id.tab3);
        spec.setIndicator("24H");
        host.addTab(spec);
        //endregion

        //region Enable Script
        wbYoutube.getSettings().setJavaScriptEnabled(true);
        wbYoutube.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wbYoutube.getSettings().setMediaPlaybackRequiresUserGesture(false);
        wbDanTri.getSettings().setJavaScriptEnabled(true);
        wbDanTri.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wb24H.getSettings().setJavaScriptEnabled(true);
        wb24H.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //endregion

//        wbYoutube.setWebViewClient(new WebViewClient());
        wbYoutube.setWebViewClient(new WebViewClient());
        wbDanTri.setWebViewClient(new WebViewClient()
        {
            public void onPageFinished(WebView view, String url) {
                reload();
            }
        });
        wb24H.setWebViewClient(new WebViewClient());

        //region Load Pagesg
        //wbYoutube.loadUrl("http://m.hdonline.vn/phim-tham-tu-lung-danh-conan-3453.html#ArvtAgTo2qhesE5E.97");
        //wbYoutube.loadUrl("http://m.hdonline.vn/phim-ansatsu-kyoushitsu-6392.html#tmeTzs0w4JiB02Hr.97");
        //wbYoutube.loadUrl("http://xemtivingon.com/");
        //wbYoutube.loadUrl("https://m.youtube.com/watch?v=UyuIGMBuRuI");
        wbYoutube.loadUrl("file:///android_asset/Data/myfriend.html");
        //wbYoutube.loadUrl("http://mobifonemap.com/myfriend.html");
        wbDanTri.loadUrl("http://dantri.com.vn/"); 
        wb24H.loadUrl("http://www.24h.com.vn/");
        //endregion

        Timer repeatTask = new Timer();
        repeatTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wbYoutube.reload();
                        wbDanTri.reload();
                        wb24H.reload();
                        Log.d("link", wbYoutube.getUrl());
                    }
                });
            }
        }, 0, 15000);
    }

    public void reload() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                reload();
                wbDanTri.reload();
                wb24H.reload();
            }
        }, 5000);
    }

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    @Override
    public void onInitializationSuccess(Provider provider, final YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(Config.YOUTUBE_VIDEO_CODE); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
            youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {

                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {

                }

                @Override
                public void onVideoEnded() {
                    youTubePlayer.play();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

}
