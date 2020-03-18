package vst.jimmy.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import vst.jimmy.db.SharepredPreferencesCls;

public class LoginActivity extends AppCompatActivity {

    private WebView mWebView;
    private SharepredPreferencesCls sharepredPreferencesCls;
    public static final String USER_NAME = "username";
    public static final String PASS_WORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mWebView = findViewById(R.id.wbLogin);

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        ButtonClickJavascriptInterface myJavaScriptInterface = new ButtonClickJavascriptInterface(LoginActivity.this);
        mWebView.addJavascriptInterface(myJavaScriptInterface, "Android");
        mWebView.loadUrl("file:///android_asset/www/login.html");

        sharepredPreferencesCls = new SharepredPreferencesCls(LoginActivity.this);
        String user = sharepredPreferencesCls.getString(USER_NAME);
        String pass = sharepredPreferencesCls.getString(PASS_WORD);

        if (user == null && pass == null )
        {
            sharepredPreferencesCls.putString(USER_NAME, "admin");
            sharepredPreferencesCls.putString(PASS_WORD, "admin");
            //Toast.makeText(MainActivity.this, "save "+ ip + "; "+ port, Toast.LENGTH_SHORT).show();
        }
    }

    //region -- Webview call --
    public class ButtonClickJavascriptInterface {
        Context mContext;

        ButtonClickJavascriptInterface(Context c) {
            mContext = c;
        }

        /**
         * Hiển thị một thông báo, được gửi từ WebView
         * @param toast
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, "Message: " + toast, Toast.LENGTH_SHORT).show();
        }

        /**
         * Hàm kiểm tra đăng nhập
         * @param _user: Tên đăng nhập
         * @param _pass: Mật khẩu
         */
        @JavascriptInterface
        public void login(String _user, String _pass)
        {
            String user = sharepredPreferencesCls.getString(USER_NAME);
            String pass = sharepredPreferencesCls.getString(PASS_WORD);

            // Kiểm tra đăng nhập, nếu đúng thì mở sang trang Main
            if (_user.equals(user) && _pass.equals(pass))
            {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }

        /**
         * Nếu đã lưu địa chỉ ip và port thì hiển thị
         * Đọc thông tin lưu trong Shareprefence, gửi sang webview
         */
        @JavascriptInterface
        public void showAccount() {
            final String user = sharepredPreferencesCls.getString(USER_NAME);
            final String pass = sharepredPreferencesCls.getString(PASS_WORD);
            //Toast.makeText(MainActivity.this, "get to show: "+ ip + "; "+ port, Toast.LENGTH_SHORT).show();
            if (user != null && pass != null)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final WebView webView = (WebView) findViewById(R.id.wbLogin);
                        String s = String.format("showAccount(\"%s\",\"%s\")",user, pass) ;
                        webView.loadUrl("javascript:"+s+";");
                    }
                });
            }
            //view.loadUrl("javascript:document.getElementById('ipaddress').value="+ip+";document.getElementById('port').value="+port+";");
        }

    }
    //endregion
}
