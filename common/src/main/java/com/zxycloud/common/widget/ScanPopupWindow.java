package com.zxycloud.common.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zxycloud.common.R;
import com.zxycloud.common.utils.TimerUtils;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public class ScanPopupWindow extends PopupWindow {
    private boolean isNFC;
    private TimerUtils timerUtils;
    private Button btn_scan;
    private static NfcAdapter mNfcAdapter;
    private static String[][] mTechList = null;
    private static PendingIntent mPendingIntent = null;
    private static IntentFilter[] mIntentFilter = null;

    private View mView; // PopupWindow 菜单布局
    private Context mContext; // 上下文参数

    public ScanPopupWindow(Context context) {
        super(context);
        this.mContext = context;

        isNFC = getInitialize(context);

        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.scan_pop_item, null);
        btn_scan = mView.findViewById(R.id.btn_scan);
        TextView tvHint = mView.findViewById(R.id.tv_hint);
        if (isNFC) {
            tvHint.setText(mContext.getText(R.string.scan_hint));
            startScan(btn_scan);
        } else {
            tvHint.setText(mContext.getString(R.string.not_nfc));
            setBtnGray(btn_scan);
        }

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNFC)
                    if (!mContext.getText(R.string.cancel_scan).equals(btn_scan.getText().toString()))
                        startScan((Button) v);
                dismiss();
            }
        });

        // 导入布局
        this.setContentView(mView);
        // 设置动画效果
        this.setAnimationStyle(R.style.popwindow_anim_style);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置可触
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);
        // 单击弹出窗以外处 关闭弹出窗
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(R.id.ll_pop).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP && y < height)
                    dismiss();
                return true;
            }
        });
    }

    private void setBtnGray(Button btn) {
        ((ImageView) (mView.findViewById(R.id.iv_nfc))).setColorFilter(Color.LTGRAY);
        btn.setText(mContext.getText(R.string.cancel_scan));
        btn.getBackground().setColorFilter(Color.parseColor("#c9c9c9"), PorterDuff.Mode.DARKEN);
    }

    private void startScan(Button v) {
        setBtnGray(v);
        openTimer();
        mNfcAdapter.enableForegroundDispatch((Activity) mContext, mPendingIntent, mIntentFilter, mTechList);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (isNFC && mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch((Activity) mContext);
        if (timerUtils != null)
            timerUtils.stop();
    }

    private void openTimer() {
        timerUtils = new TimerUtils(3600000, 120000, new TimerUtils.OnBaseTimerCallBack() {
            @Override
            public void onTick(Object tag, long millisUntilFinished) {
                System.gc();
            }

            @Override
            public void onFinish(Object tag) {
                timerUtils.stop();
                btn_scan.setText(mContext.getString(R.string.rescan));
                //关闭前台调度系统
                if (isNFC) {
                    mNfcAdapter.disableForegroundDispatch((Activity) mContext);
                }
            }
        });
        timerUtils.start();
    }

    public void show(View view) {
        showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public static boolean getInitialize(Context activity) {
        mNfcAdapter = NfcCheck(activity);
        NfcInit(activity);
        return mNfcAdapter != null;
    }

    /**
     * 检查NFC是否打开
     */
    public static NfcAdapter NfcCheck(Context activity) {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null)
            return null;
        else if (!mNfcAdapter.isEnabled()) {
            Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
            activity.startActivity(setNfc);
        }
        return mNfcAdapter;
    }

    /**
     * 初始化nfc设置
     */
    public static void NfcInit(Context activity) {
        mTechList = null;
        mPendingIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter filter2 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        mIntentFilter = new IntentFilter[]{filter, filter2};
    }

    /**
     * 读取NFC的数据
     */
    public static String readNFCFromTag(Intent intent) {
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawArray != null) {
            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
            NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
            if (mNdefRecord != null) {
                String readResult = null;
                try {
                    readResult = new String(mNdefRecord.getPayload(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // 将“ZXY01”前的字符去除，如enZXY01121211111转成ZXY01121211111
                readResult = readResult.replace(new StringTokenizer(readResult, "ZXY01").nextToken(),"");
                return readResult;
            }
        }
        return "";
    }

    /**
     * 读取nfcID
     */
    public static String readNFCId(Intent intent) {
        return ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
    }

    /**
     * 将字节数组转换为字符串
     */
    private static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";
        if (inarray != null)
            for (j = 0; j < inarray.length; ++j) {
                in = (int) inarray[j] & 0xff;
                i = (in >> 4) & 0x0f;
                out += hex[i];
                i = in & 0x0f;
                out += hex[i];
            }
        return out;
    }

}
