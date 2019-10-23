package com.zxycloud.zszw.fragment.mine.profile;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.zxycloud.zszw.R;
import com.zxycloud.zszw.base.BaseBackFragment;
import com.zxycloud.common.utils.CommonUtils;
import com.zxycloud.common.utils.FileUtils;
import com.zxycloud.common.utils.PermissionUtils;
import com.zxycloud.common.utils.SPUtils;
import com.zxycloud.common.widget.PhotoPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileFragment extends BaseBackFragment implements View.OnClickListener{
    private int ADD_OR_MODIFY_MAILBOXES = 101;// 添加或修改邮箱
    private int ADD_OR_MODIFY_PHONE = 102;// 添加或修改手机号码
    static final String EMAIL_ADDRESS = "email_address";
    static final String PHONE_NUMBER = "phone_number";
    private EditText editEmail, editPhone, editBirthday, editSex, editPosition, editName, editLoginID;
    private ImageView imgHeader;
    private List<String> originalList, newerList;
    private String[] sexArry;// 性别选择
    private String imgPath;// 头像本地地址
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;//本地
    private static final int CODE_CAMERA_REQUEST = 0xa1;//拍照
    private PhotoPopupWindow mPhotoPopupWindow;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mine_profile;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        sexArry = new String[] {getResources().getString(R.string.common_string_female), getResources().getString(R.string.common_string_male)};

        setToolbarTitle(R.string.device_base_information).initToolbarNav();

        // TODO: 2019/6/14 暂时不能修改信息，所以隐藏
        /*addNavigationBack(new OnNavigationListener() {
            @Override
            public boolean addNavigationBack() {
                addList(newerList);
                for (int i = 0; i < originalList.size(); i++) {
                    if (! originalList.get(i).equals(newerList.get(i))) {
                        // 此处弹窗：问用户是否保存编辑信息
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle("退出编辑")
                                .setMessage("是否保存变更信息？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // TODO 在此处提交变更过后的信息，然后退出
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // 退出该页面
                                        finish();
                                        return;
                                    }
                                }).create();
                        alertDialog.show();
                        return false;
                    }
                }
                return true;
            }
        });*/

        imgHeader = findViewById(R.id.img_header);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        editBirthday = findViewById(R.id.edit_birthday);
        editSex = findViewById(R.id.edit_sex);
        editPosition = findViewById(R.id.edit_position);
        editLoginID = findViewById(R.id.edit_login_id);
        editName = findViewById(R.id.edit_name);

        SPUtils spUtils = CommonUtils.getSPUtils(_mActivity);

        editEmail.setText(spUtils.getString(SPUtils.USER_EMAIL, "-"));
        editPhone.setText(spUtils.getString(SPUtils.USER_PHONE, "-"));
        editName.setText(spUtils.getString(SPUtils.USER_NAME, "-"));

        setEnable(editEmail
                , editPhone
                , editName);

        // 设置EditText无法获取焦点，无法打开软键盘
        setEditType(editPosition, editSex, editBirthday, editPhone, editEmail);

        // 设置点击事件
        setOnClickListener(this, R.id.edit_email, R.id.edit_phone, R.id.edit_birthday, R.id.edit_sex, R.id.edit_position, R.id.img_header);

        initData();
    }

    private void setEditType(EditText... edits) {
        for (EditText edit : edits) {
            edit.setInputType(InputType.TYPE_NULL);
            edit.setFocusable(false);
        }
    }

    private void setEnable(EditText... edits) {
        for (EditText edit : edits) {
            edit.setEnabled(false);
        }
    }

    private void initData() {
        originalList = new ArrayList<>();
        newerList = new ArrayList<>();
        addList(originalList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_email:
                startForResult(ReEmailAddsFragment.newInstance(editEmail.getText().toString()), ADD_OR_MODIFY_MAILBOXES);
                break;
            case R.id.edit_phone:
                startForResult(RePhoneNumFragment.newInstance(editPhone.getText().toString()), ADD_OR_MODIFY_PHONE);
                break;
            case R.id.edit_birthday:
                showDatePickerDialog();
                break;
            case R.id.edit_sex:
                showSexChooseDialog();
                break;
            case R.id.edit_position:
                break;
            case R.id.img_header:
                showReplaceHeadPopupWindow();
                break;
        }
    }

    private void addList(List<String> profileList) {
        profileList.add(imgHeader.getDrawable().toString());
        profileList.add(editBirthday.getText().toString().trim());
        profileList.add(editEmail.getText().toString().trim());
        profileList.add(editPhone.getText().toString().trim());
        profileList.add(editSex.getText().toString().trim());
        profileList.add(editPosition.getText().toString().trim());
        profileList.add(editLoginID.getText().toString().trim());
        profileList.add(editName.getText().toString().trim());
    }

    /**
     * 展示日期选择对话框
     */
    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editBirthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    /**
     * 展示性别选中对话框
     */
    private void showSexChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setSingleChoiceItems(sexArry, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editSex.setText(sexArry[which]);
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 显示替换头像选择器
     */
    private void showReplaceHeadPopupWindow() {
        // 进入相册选择
        mPhotoPopupWindow = new PhotoPopupWindow(getContext(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.setRequestPermissions(ProfileFragment.this, new PermissionUtils.PermissionGrant() {
                    @Override
                    public Integer[] onPermissionGranted() {
                        return new Integer[]{PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE};
                    }

                    @Override
                    public void onRequestResult(List<String> deniedPermission) {
                        if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                            Intent inImg = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(inImg, CODE_GALLERY_REQUEST);
                        }
                    }
                });
                mPhotoPopupWindow.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开启相机
                PermissionUtils.setRequestPermissions(ProfileFragment.this, new PermissionUtils.PermissionGrant() {
                    @Override
                    public Integer[] onPermissionGranted() {
                        return new Integer[]{PermissionUtils.CODE_CAMERA, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE};
                    }

                    @Override
                    public void onRequestResult(List<String> deniedPermission) {
                        if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                            imgPath = FileUtils.getNewFileDir(FileUtils.IMAGE_CAPTURE);
                            if (TextUtils.isEmpty(imgPath))
                                return;
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                File path = new File(imgPath);
                                if (! path.exists()) {
                                    path.mkdirs();
                                }
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri uri;
                                if (Build.VERSION.SDK_INT >= 24) {
                                    uri = FileProvider.getUriForFile(getContext().getApplicationContext(), "com.zxycloud.zszw.fileprovider", path);
                                } else {
                                    uri = Uri.fromFile(path);
                                }
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(intent, CODE_CAMERA_REQUEST);
                            }
                        }
                    }
                });
                mPhotoPopupWindow.dismiss();
            }
        });
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null);
        mPhotoPopupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {//取消
            return;
        }
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:// 相册
                Uri uriImg = data.getData();//得到uri，后面就是将uri转化成file的过程。
                String imgPath = FileUtils.getFilePathByUri(getContext(), uriImg);
                if (! TextUtils.isEmpty(imgPath)) {
                    File img = new File(imgPath);
                    this.imgPath = img.toString();
                }
                CommonUtils.glide().loadImageView(getContext(), this.imgPath, imgHeader);
                break;
            case CODE_CAMERA_REQUEST:// 拍照
                CommonUtils.glide().loadImageView(getContext(), this.imgPath, imgHeader);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == ADD_OR_MODIFY_MAILBOXES && resultCode == RESULT_OK && data != null) {
            editEmail.setText(data.getString(EMAIL_ADDRESS));
        } else if (requestCode == ADD_OR_MODIFY_PHONE && resultCode == RESULT_OK && data != null) {
            editPhone.setText(data.getString(PHONE_NUMBER));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
