package moe.ono.creator;

import static moe.ono.hooks.protocol.QPacketHelperKt.sendPacket;
import static moe.ono.util.QAppUtils.PeerUidToUserUin;
import static moe.ono.util.QAppUtils.getCurrentUin;
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
import moe.ono.hooks.base.util.Toasts;
import moe.ono.ui.CommonContextWrapper;
import moe.ono.util.AppRuntimeHelper;
import moe.ono.util.ContextUtils;

@SuppressLint("ResourceType")
public class GetRTCArkDialog extends BottomPopupView {
    private static BasePopupView popupView;

    public GetRTCArkDialog(@NonNull Context context) {
        super(context);
    }

    public static void createView(Context context) {
        Context fixContext = CommonContextWrapper.createAppCompatContext(context);
        XPopup.Builder NewPop = new XPopup.Builder(fixContext).moveUpToKeyboard(true).isDestroyOnDismiss(true);
        NewPop.maxHeight((int) (XPopupUtils.getScreenHeight(context) * .7f));
        NewPop.popupHeight((int) (XPopupUtils.getScreenHeight(context) * .63f));


        reportVisitor(AppRuntimeHelper.getAccount(), "CreateView-GetRTCArkDialog");

        popupView = NewPop.asCustom(new GetRTCArkDialog(fixContext));
        popupView.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate() {
        super.onCreate();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Button btnSend = findViewById(R.id.btn_send);
            TextView tvTarget = findViewById(R.id.tv_target);

            EditText group_name = findViewById(R.id.group_name);
            EditText inviter_name = findViewById(R.id.inviter_name);
            EditText group_id = findViewById(R.id.group_id);

            group_name.setVisibility(VISIBLE);
            group_id.setVisibility(VISIBLE);
            inviter_name.setVisibility(VISIBLE);

            group_name.clearFocus();
            group_id.clearFocus();
            inviter_name.clearFocus();

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

                String req_body = "{\n" +
                        "  \"1\": "+getCurrentUin()+",\n" +
                        "  \"2\": 4981798,\n" +
                        "  \"3\": "+System.currentTimeMillis()+",\n" +
                        "  \"4\": "+(chat_type - 1)+",\n" +
                        "  \"5\": "+uin+",\n" +
                        "  \"6\": \""+group_name.getText().toString()+"\",\n" +
                        "  \"7\": \""+inviter_name.getText().toString()+"\",\n" +
                        "  \"8\": 1,\n" +
                        "  \"9\": "+((group_id.getText().toString().isEmpty()) ? uin : group_id.getText().toString())+"\n" +
                        "}";

                sendPacket("QQRTCSvc.push_share_ark", req_body);
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
        return R.layout.get_rtc_ark;
    }
}


