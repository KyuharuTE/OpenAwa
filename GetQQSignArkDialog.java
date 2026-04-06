package moe.ono.creator;

import static moe.ono.util.QAppUtils.PeerUidToUserUin;
import static moe.ono.util.Session.getCurrentChatType;
import static moe.ono.util.Session.getCurrentPeerID;
import static moe.ono.util.analytics.ActionReporter.reportVisitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import java.io.IOException;

import moe.ono.R;
import moe.ono.hooks.base.util.Toasts;
import moe.ono.hooks.item.developer.GetCookie;
import moe.ono.ui.CommonContextWrapper;
import moe.ono.util.AppRuntimeHelper;
import moe.ono.util.ContextUtils;
import moe.ono.util.SyncUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressLint("ResourceType")
public class GetQQSignArkDialog extends BottomPopupView {
    private static BasePopupView popupView;

    public GetQQSignArkDialog(@NonNull Context context) {
        super(context);
    }

    public static void createView(Context context) {
        Context fixContext = CommonContextWrapper.createAppCompatContext(context);
        XPopup.Builder NewPop = new XPopup.Builder(fixContext).moveUpToKeyboard(true).isDestroyOnDismiss(true);
        NewPop.maxHeight((int) (XPopupUtils.getScreenHeight(context) * .7f));
        NewPop.popupHeight((int) (XPopupUtils.getScreenHeight(context) * .63f));


        reportVisitor(AppRuntimeHelper.getAccount(), "CreateView-GetQQSignArkDialog");

        popupView = NewPop.asCustom(new GetQQSignArkDialog(fixContext));
        popupView.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate() {
        super.onCreate();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Button btnSend = findViewById(R.id.btn_send);
            TextView tvTarget = findViewById(R.id.tv_target);

            EditText title = findViewById(R.id.ark_title);
            EditText desc = findViewById(R.id.ark_desc);
            EditText jump_url = findViewById(R.id.jump_url);
            EditText ark_preview_url = findViewById(R.id.ark_preview_url);

            title.setVisibility(VISIBLE);
            desc.setVisibility(VISIBLE);
            jump_url.setVisibility(VISIBLE);
            ark_preview_url.setVisibility(VISIBLE);

            title.clearFocus();
            desc.clearFocus();
            jump_url.clearFocus();
            ark_preview_url.clearFocus();

            int chat_type = getCurrentChatType();
            if (chat_type == 1) {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "好友");
            } else if (chat_type == 2) {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "群聊");
            } else {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "未知");
            }

            btnSend.setOnClickListener(v -> {
                String uin;
                if (chat_type == 1) {
                    uin = String.valueOf(PeerUidToUserUin(getCurrentPeerID()));
                } else if (chat_type == 2) {
                    uin = getCurrentPeerID();
                } else {
                    Toasts.error(ContextUtils.getCurrentActivity(), "不支持当前对话");
                    popupView.dismiss();
                    return;
                }

                String cookie = GetCookie.Companion.getCookie("ti.qq.com");
                assert cookie != null;
                String gtk = GetCookie.Companion.getBknByCookie(cookie);

                OkHttpClient client = new OkHttpClient();
                String json = "{\n" +
                        "  \"proxy\": \"Share\",\n" +
                        "  \"command\": \"ShareCard\",\n" +
                        "  \"data\": {\n" +
                        "    \"recv_uin\": \""+uin+"\",\n" +
                        "    \"share_type\": "+(chat_type - 1)+",\n" +
                        "    \"content\": {\n" +
                        "      \"title\": \""+title.getText().toString()+"\",\n" +
                        "      \"desc\": \""+desc.getText().toString()+"\",\n" +
                        "      \"preview\": \""+ark_preview_url.getText().toString()+"\",\n" +
                        "      \"jump_url\": \""+jump_url.getText().toString()+"\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(json, mediaType);
                Request request = new Request.Builder().url("https://ti.qq.com/v2/signin/trpc?gt_k=" + gtk)
                        .header("Host", "ti.qq.com")
                        .header("User-Agent", "24117RK2CC Build/AQ3A.240829.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/139.0.7258.143 Mobile Safari/537.36 V1_AND_SQ_9.1.35_8708_YYB_D QQ/9.1.35.22670 NetType/WIFI WebP/0.4.1 AppId/537265587 Pixel/1440 StatusBarHeight/138 SimpleUISwitch/0 QQTheme/1103 StudyMode/0 CurrentMode/0 CurrentFontScale/1.0 GlobalDensityScale/0.96 AllowLandscape/false InMagicWin/0")
                        .header("X-Request-With", "com.tencent.mobileqq")
                        .header("Cookie", cookie)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        SyncUtils.runOnUiThread(() -> Toasts.error(v.getContext(), "网络错误"));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        SyncUtils.runOnUiThread(() -> Toasts.success(v.getContext(), "请求成功"));
                    }
                });

                popupView.dismiss();
            });
        }, 100);


    }




    @Override
    protected void beforeDismiss() {
        super.beforeDismiss();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.get_qq_sign_ark;
    }
}


