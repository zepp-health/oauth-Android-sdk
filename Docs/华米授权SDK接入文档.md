快速开始
===
##### 1) 在应用的AndroidManifest.xml里添加以下配置：

```
<uses-permission android:name="android.permission.INTERNET" />
```

##### 2) 获取AccessToken

```
// 参数解释：
// APP_ID开发者预先申请好
// 1.创建OpenAuthorize对象
mOpenAuthorize = new OpenAuthorize(context)
    .secretEnable(false) // 是否安全加密,一般选择false即可
    .setAppId(APP_ID)
    .setAuthCallback(new Callback<AuthResults>() {
        @Override
        public void onResults(AuthResults results) {
            if(results.hasError()){
                int errorCode = results.getErrorCode();
                String errorMsg = results.getErrorMessage();
            }else{
                String accessToken = results.getAccessToken();
                String refreshToken = results.getRefreshToken();
                int expiresIn = results.getExpiresIn();
                String tokenType = results.getTokenType();
                String region = results.getRegion();
                // App保存以上信息，region和refreshToken参数用于刷新token操作见openApi文档
            }
        }
    });
// 2.接收数据配置
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mOpenAuthorize.onActivityResult(requestCode,resultCode,data);
}
// 3.获取AccessToken
mOpenAuthorize.startGetAccessToken(activity);
```

错误码定义
===
| 错误码 | 说明 | 
| :---: | :---- | 
| 10001 | 未支持操作错误        |
| 10002 | 内部程序出错          | 
| 10003 | 校验签名错误          | 
| 10004 | 用户未登录            | 
| 10005 | 网络无法访问          | 
| 10006 | 服务返回未知错误      | 
| 10007 | 用户取消操作          | 
| 10008 | 未配置访问权限错误    | 
| 10009 | APPID未配置错误       | 
| 10010 | 目标APP版本不支持     | 
| 10011 | 未收到数据错误        | 
| 10012 | 数据解析失败          | 
| 10013 | 当前用户区域设置错误  | 
| 10014 | 目标APP未安装         | 
| 10015 | 参数检查错误         | 


如何获取APK签名信息
===
##### 1）运行keytool命令
```
keytool -exportcert -alias 'Nom Nom Eat' -keystore nomnom.jks |openssl sha1 -binary | openssl base64
```
注意事项：<br />
1，使用上述命令请替换-alias 和 -keystore后面的部分，只需要替换这两处即可。<br />
2，-alias后面跟的是自己的keystore新建时对应的alias，-keystore后面是签名的文件(jks或keystore格式）。

##### 2）安装OauthSample工具
输入你的应用包名如：com.aa.cc，点击计算按钮。

SDK开发资料
===
[资料下载](https://github.com/liutz/oauth-Android-sdk)

更多OAuth资料？
===
https://github.com/huamitech/rest-api/wiki