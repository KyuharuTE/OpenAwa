package moe.ono.creator;

import static moe.ono.hooks.protocol.QPacketHelperKt.sendPacket;
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

import moe.ono.R;
import moe.ono.bridge.ntapi.ChatTypeConstants;
import moe.ono.hooks.base.util.Toasts;
import moe.ono.ui.CommonContextWrapper;
import moe.ono.util.AppRuntimeHelper;
import moe.ono.util.ContextUtils;

@SuppressLint("ResourceType")
public class GetTeamUpArkDialog extends BottomPopupView {
    private static BasePopupView popupView;

    public GetTeamUpArkDialog(@NonNull Context context) {
        super(context);
    }

    public static void createView(Context context) {
        Context fixContext = CommonContextWrapper.createAppCompatContext(context);
        XPopup.Builder NewPop = new XPopup.Builder(fixContext).moveUpToKeyboard(true).isDestroyOnDismiss(true);
        NewPop.maxHeight((int) (XPopupUtils.getScreenHeight(context) * .7f));
        NewPop.popupHeight((int) (XPopupUtils.getScreenHeight(context) * .63f));


        reportVisitor(AppRuntimeHelper.getAccount(), "CreateView-GetTeamUpArkDialog");

        popupView = NewPop.asCustom(new GetTeamUpArkDialog(fixContext));
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
            EditText time = findViewById(R.id.time);

            title.setVisibility(VISIBLE);
            desc.setVisibility(VISIBLE);
            time.setVisibility(VISIBLE);

            title.clearFocus();
            desc.clearFocus();
            time.clearFocus();

            int chat_type = getCurrentChatType();
            if (chat_type == 1) {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "好友");
            } else if (chat_type == 2) {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "群聊");
            } else {
                tvTarget.setText("当前会话: " + getCurrentPeerID() + " | " + "未知");
            }

            btnSend.setOnClickListener(v -> {
                if (chat_type != ChatTypeConstants.GROUP) {
                    Toasts.error(ContextUtils.getCurrentActivity(), "仅支持群聊会话");
                    return;
                }

                String uin;
                uin = getCurrentPeerID();

                String req_body = "{\n" +
                        "  \"1\": 37403,\n" +
                        "  \"2\": 0,\n" +
                        "  \"4\": {\n" +
                        "    \"1\": {\n" +
                        "      \"1\": {},\n" +
                        "      \"2\": "+uin+",\n" +
                        "      \"3\": \""+title.getText().toString()+"\",\n" +
                        "      \"4\": \""+desc.getText().toString()+"\",\n" +
                        "      \"5\": 0,\n" +
                        "      \"6\": "+time.getText().toString()+",\n" +
                        "      \"8\": 0,\n" +
                        "      \"9\": 1,\n" +
                        "      \"10\": 200,\n" +
                        "      \"11\": 0,\n" +
                        "      \"12\": 0,\n" +
                        "      \"13\": {\n" +
                        "        \"1\": 0,\n" +
                        "        \"2\": {},\n" +
                        "        \"3\": {}\n" +
                        "      },\n" +
                        "      \"14\": 0,\n" +
                        "      \"15\": 0,\n" +
                        "      \"16\": 0\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"12\": 1\n" +
                        "}";

                sendPacket("OidbSvcTrpcTcp.0x921b_0", req_body);
                Toasts.success(ContextUtils.getCurrentActivity(), "请求成功");

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
        return R.layout.get_team_up_ark;
    }
}


