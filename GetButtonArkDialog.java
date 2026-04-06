package moe.ono.creator;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import moe.ono.R;
import moe.ono.hooks.base.util.Toasts;
import moe.ono.hooks.item.developer.GetCookie;
import moe.ono.hooks.protocol.QPacketHelperKt;
import moe.ono.ui.CommonContextWrapper;
import moe.ono.util.AppRuntimeHelper;
import moe.ono.util.Logger;
import moe.ono.util.Session;
import moe.ono.util.SyncUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressLint("ResourceType")
public class GetButtonArkDialog extends BottomPopupView {
    private static BasePopupView popupView;
    public static boolean is_awa = false;

    public GetButtonArkDialog(@NonNull Context context) {
        super(context);
    }

    public static void createView(Context context) {
        Context fixContext = CommonContextWrapper.createAppCompatContext(context);
        XPopup.Builder NewPop = new XPopup.Builder(fixContext).moveUpToKeyboard(true).isDestroyOnDismiss(true);
        NewPop.maxHeight((int) (XPopupUtils.getScreenHeight(context) * .7f));
        NewPop.popupHeight((int) (XPopupUtils.getScreenHeight(context) * .63f));


        reportVisitor(AppRuntimeHelper.getAccount(), "CreateView-GetButtonArkDialog");

        popupView = NewPop.asCustom(new GetButtonArkDialog(fixContext));
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
            EditText jump_url = findViewById(R.id.jump_url);
            EditText ark_preview_url = findViewById(R.id.ark_preview_url);
            EditText ark_tag_name = findViewById(R.id.ark_tag_name);
            EditText ark_tag_icon = findViewById(R.id.ark_tag_icon);
            EditText ark_prompt = findViewById(R.id.ark_prompt);
            EditText ark_button_title = findViewById(R.id.ark_button_title);
            EditText ark_button_url = findViewById(R.id.ark_button_url);
            EditText ark_button_disable = findViewById(R.id.ark_button_disable);
            EditText ark_button_status = findViewById(R.id.ark_button_status);


            title.setVisibility(VISIBLE);
            jump_url.setVisibility(VISIBLE);
            ark_preview_url.setVisibility(VISIBLE);
            ark_tag_name.setVisibility(VISIBLE);
            ark_tag_icon.setVisibility(VISIBLE);
            ark_prompt.setVisibility(VISIBLE);
            ark_button_title.setVisibility(VISIBLE);
            ark_button_url.setVisibility(VISIBLE);
            ark_button_disable.setVisibility(VISIBLE);
            ark_button_status.setVisibility(VISIBLE);

            title.clearFocus();
            jump_url.clearFocus();
            ark_preview_url.clearFocus();
            ark_tag_name.clearFocus();
            ark_tag_icon.clearFocus();
            ark_prompt.clearFocus();
            ark_button_title.clearFocus();
            ark_button_url.clearFocus();
            ark_button_disable.clearFocus();
            ark_button_status.clearFocus();

            int chat_type = getCurrentChatType();
            if (chat_type == 1) {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "好友");
            } else if (chat_type == 2) {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "群聊");
            } else {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "未知");
            }

            btnSend.setOnClickListener(v -> {
                try {
                    String body = "{\n" +
                            "  \"1\": \"trpc.shadow_qq.message_center.MessageCenter\",\n" +
                            "  \"2\": \"\\/trpc.shadow_qq.message_center.MessageCenter\\/GetArkEventSignature\",\n" +
                            "  \"3\": \"{\\\"title\\\":\\\""+title.getText().toString()+
                            "\\\",\\\"jumpURL\\\":\\\""+jump_url.getText().toString()+
                            "\\\",\\\"preview\\\":\\\""+ark_preview_url.getText().toString()+
                            "\\\",\\\"tag\\\":\\\""+((ark_tag_name.getText().toString().isEmpty()) ? "QQ频道" : ark_tag_name.getText().toString())+
                            "\\\",\\\"tagIcon\\\":\\\""+((ark_tag_icon.getText().toString().isEmpty()) ? "https://tianxuan.gtimg.cn/47329_bd95e16e/assets/guild-icon.png" : ark_tag_icon.getText().toString())+
                            "\\\",\\\"prompt\\\":\\\""+((ark_prompt.getText().toString().isEmpty()) ? title.getText().toString() : ark_prompt.getText().toString())+
                            "\\\",\\\"buttonNum\\\":1,\\\"buttonTitle\\\":[\\\""+ark_button_title.getText().toString()+
                            "\\\"],\\\"buttonURL\\\":[\\\""+((ark_button_url.getText().toString().isEmpty()) ? jump_url.getText().toString() : ark_button_url.getText().toString())+
                            "\\\"],\\\"buttonDisable\\\":["+((ark_button_disable.getText().toString().isEmpty()) ? "false" : ark_button_disable.getText().toString())+
                            "],\\\"buttonStatus\\\":[\\\""+((ark_button_status.getText().toString().isEmpty()) ? "primary" : ark_button_status.getText().toString())+"\\\"]}\"\n" +
                            "}";
                    is_awa = true;
                    QPacketHelperKt.sendPacket("trpc.ecom.api_gateway.ApiGateway.SsoForward", body);
                    Toasts.info(v.getContext(), "请求成功");
                } catch (Exception e) {
                    Logger.e(e);
                }

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
        return R.layout.get_button_ark;
    }
}


