package com.huami.android.oauth.sample;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.huami.android.oauth.Callback;
import com.huami.android.oauth.AuthResults;
import com.huami.android.oauth.OpenAuthorize;
import com.huami.android.oauth.Utils;
import com.huami.android.oauth.entity.SingInfo;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private OpenAuthorize mOpenAuthorize;
    private TextView mConsoleView;
    private EditText mGetPkgEdit;
    private CheckBox mSecretCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConsoleView = (TextView) findViewById(R.id.console_info);
        mGetPkgEdit = (EditText) findViewById(R.id.input_pkg_get_token);
        mSecretCheckBox = (CheckBox) findViewById(R.id.checkbox_s);
        mSecretCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mOpenAuthorize != null){
                    mOpenAuthorize.secretEnable(isChecked);
                }
            }
        });
        // 测试方便
        findViewById(R.id.getToken).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mGetPkgEdit.setText("com.huami.passport.sample");
                return false;
            }
        });

        // 客户接入参考，初始化
        mOpenAuthorize = new OpenAuthorize(this)
            .setAppId(Constants.APP_ID)
            .secretEnable(mSecretCheckBox.isChecked())
            .setAuthCallback(new Callback<AuthResults>() {
                @Override
                public void onResults(AuthResults results) {
                    String info;
                    // 出错
                    if(results.hasError()){
                        int errorCode = results.getErrorCode();
                        info = "Error code=" + errorCode + " ,message="+results.getErrorMessage();
                        // 错误码定义见接入文档
                        // 注意：授权App未安装情况,三方可以自由定义及引导，简单做法就是跳转浏览器引导用户下载
                        if(errorCode == 10014 || errorCode == 10010){
                            try {
                                String downloadUrl = mOpenAuthorize.getDownloadUrl();
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(downloadUrl);
                                intent.setData(content_url);
                                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                                startActivity(intent);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }else{
                        // 成功,应用保存该信息
                        String accessToken = results.getAccessToken();
                        mRefreshToken = results.getRefreshToken();
                        int expiresIn = results.getExpiresIn();
                        String tokenType = results.getTokenType();
                        info = "Success accessToken=" + accessToken
                                + ",refreshToken="+mRefreshToken
                                + ",expiresIn="+expiresIn
                                + ",tokenType="+tokenType;

                        /**
                         * 也可以自己解析，加密操作解析json&不加密解析url
                         */
                        String payload = results.getSuccessPayload();
                        Log.d(TAG,"SuccessPayload:"+payload);
                    }

                    Log.d(TAG,info);
                    mConsoleView.setText(info);
                }
            });


    }

    // 测试计算应用签名信息
    public void onGetSignature(View view){
        EditText pkgEdit = (EditText)findViewById(R.id.input_pkg);
        String pkgName = pkgEdit.getText().toString();
        boolean pkgNameNull = false;

        if(TextUtils.isEmpty(pkgName)){
            pkgNameNull = true;
            pkgName = this.getPackageName();
        }
        SingInfo singInfo = Utils.getSingInfo(getApplicationContext(),pkgName);
        String keyHash;
        if(singInfo != null && !TextUtils.isEmpty(keyHash = singInfo.keyHash)){
            mConsoleView.setText(pkgNameNull ? "当前应用KeyHash:"+keyHash:keyHash);
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(keyHash.trim());
            Toast.makeText(this,"签名信息已复制剪贴板",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 接收返回数据
        mOpenAuthorize.onActivityResult(requestCode,resultCode,data);
    }

    public void onGetToekn(View view){
        // 注：为了测试使用，三方客户接入忽略
        String targetApp = mGetPkgEdit.getText().toString();
        mOpenAuthorize.targetApp(targetApp);
        // 点击获取Accesstoken
        mOpenAuthorize.startGetAccessToken(this);
    }

    private String mRefreshToken;
    private String mRegion;
}
