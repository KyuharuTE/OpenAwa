package moe.ono.creator;

import static moe.ono.hooks.protocol.QPacketHelperKt.sendPacket;
import static moe.ono.util.Session.getCurrentChatType;
import static moe.ono.util.Session.getCurrentPeerID;
import static moe.ono.util.analytics.ActionReporter.reportVisitor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import moe.ono.R;
import moe.ono.ui.CommonContextWrapper;
import moe.ono.util.AppRuntimeHelper;

@SuppressLint("ResourceType")
public class GetRecommendContactArkDialog extends BottomPopupView {
    private static BasePopupView popupView;
    public static boolean is_awa = false;

    public GetRecommendContactArkDialog(@NonNull Context context) {
        super(context);
    }

    public static void createView(Context context) {
        Context fixContext = CommonContextWrapper.createAppCompatContext(context);
        XPopup.Builder NewPop = new XPopup.Builder(fixContext).moveUpToKeyboard(true).isDestroyOnDismiss(true);
        NewPop.maxHeight((int) (XPopupUtils.getScreenHeight(context) * .7f));
        NewPop.popupHeight((int) (XPopupUtils.getScreenHeight(context) * .63f));


        reportVisitor(AppRuntimeHelper.getAccount(), "CreateView-GetRecommendContactArkDialog");

        popupView = NewPop.asCustom(new GetRecommendContactArkDialog(fixContext));
        popupView.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate() {
        super.onCreate();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TextView tvTarget = findViewById(R.id.tv_target);

            int chat_type = getCurrentChatType();
            if (chat_type == 1) {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "好友");
            } else if (chat_type == 2) {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "群聊");
            } else {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "未知");
            }

            Button btn_send = findViewById(R.id.btn_send);
            EditText uin = findViewById(R.id.uin);
            EditText scheme = findViewById(R.id.scheme);

            uin.setVisibility(VISIBLE);
            uin.clearFocus();

            scheme.setVisibility(VISIBLE);
            scheme.clearFocus();

            btn_send.setOnClickListener(view -> {
                String req_body = "{\n" +
                        "  \"1\": 4790,\n" +
                        "  \"2\": 0,\n" +
                        "  \"4\": {\n" +
                        "    \"1\": "+ uin.getText().toString() +",\n" +
                        "    \"3\": \""+ scheme.getText().toString() +"\"\n" +
                        "  },\n" +
                        "  \"6\": \"android 9.1.35\"\n" +
                        "}\n";

                is_awa = true;
                sendPacket("OidbSvcTrpcTcp.0x11ca_0", req_body);

                dismiss();
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
        return R.layout.get_recommend_contact_ark_dialog;
    }
}
