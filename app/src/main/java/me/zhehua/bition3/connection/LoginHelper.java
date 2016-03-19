package me.zhehua.bition3.connection;

import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import me.zhehua.bition3.events.LoginFailureEvent;
import me.zhehua.bition3.events.LoginSuccessEvent;
import me.zhehua.bition3.utils.MD5;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class LoginHelper {

    public final static String TAG = "LoginHelper";

    public final static String RESPONSE_USER_TAB_ERROR = "user_tab_error";
    public final static String RESPONSE_USERNAME_ERROR = "username_error";
    public final static String RESPONSE_NON_AUTH_ERROR = "non_auth_error";
    public final static String RESPONSE_PASSWORD_ERROR = "password_error";
    public final static String RESPONSE_STATUS_ERROR = "status_error";
    public final static String RESPONSE_AVAILABLE_ERROR = "available_error";
    public final static String RESPONSE_IP_EXIST_ERROR = "ip_exist_error";
    public final static String RESPONSE_USERNUM_ERROR = "usernum_error";
    public final static String RESPONSE_ONLINE_NUM_ERROR = "online_num_error";
    public final static String RESPONSE_MODE_ERROR = "mode_error";
    public final static String RESPONSE_TIME_POLICY_ERROR = "time_policy_error";
    public final static String RESPONSE_FLUX_ERROR = "flux_error";
    public final static String RESPONSE_MINUTES_ERROR = "minutes_error";
    public final static String RESOPNSE_IP_ERROR = "ip_error";
    public final static String RESPONSE_MAC_ERROR = "mac_error";
    public final static String RESPONSE_SYNC_ERROR = "sync_error";
    public final static String RESPONSE_LOGIN_OK = "login_ok";

    public final static String RESPONSE_KEEP_ALIVE_OK = "keeplive_ok";
    public final static String RESPONSE_DROP_ERROR = "drop_error";

    public final static String RESPONSE_LOGOUT_OK = "logout_ok";
    public final static String RESPONSE_LOGOUT_ERROR = "logout_error";

    public static Observable<String> asyncLogin(@NonNull final String username, @NonNull final String psw) {
        Log.i(TAG, "start to login.");
        final String url = "http://10.0.0.55/cgi-bin/do_login";
        final OkHttpClient client = new OkHttpClient();

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                RequestBody requestBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", MD5.getMD516(psw).toLowerCase())
                        .add("drop", "0")
                        .add("type", "1")
                        .add("n", "100")
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseContent = response.body().string();
                    response.body().close();
                    Log.i(TAG, "get response : " + responseContent);
                    subscriber.onNext(responseContent);
                    if (responseContent.equals(RESPONSE_LOGIN_OK)) {
                        EventBus.getDefault().post(new LoginSuccessEvent());
                    } else {
                        EventBus.getDefault().post(new LoginFailureEvent());
                    }
                } catch (IOException e) {
                    subscriber.onError(e);
                    EventBus.getDefault().post(new LoginFailureEvent());
                    Log.e(TAG, "login error");
                }
            }
        }).subscribeOn(Schedulers.io());

    }

    public static Observable<String> asyncLogout() {
        Log.i(TAG, "try logout");
        return Observable.just(LoginHelper.RESPONSE_LOGOUT_OK);
    }
}
