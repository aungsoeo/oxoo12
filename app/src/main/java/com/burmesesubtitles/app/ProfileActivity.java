package com.burmesesubtitles.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.burmesesubtitles.app.database.DatabaseHelper;
import com.burmesesubtitles.app.network.RetrofitClient;
import com.burmesesubtitles.app.network.apis.DeactivateAccountApi;
import com.burmesesubtitles.app.network.apis.ProfileApi;
import com.burmesesubtitles.app.network.model.ResponseStatus;
import com.burmesesubtitles.app.network.model.User;

import com.burmesesubtitles.app.utils.Constants;
import com.burmesesubtitles.app.utils.FileUtil;
import com.burmesesubtitles.app.utils.PreferenceUtils;
import com.burmesesubtitles.app.utils.ToastMsg;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPass, genderSpinner;
    private Button btnUpdate, deactivateBt;
    private ProgressDialog dialog;
    private String URL = "", strGender;
    private CircleImageView userIv;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri imageUri;
    private ProgressBar progressBar;
    private String id;
    private boolean isDark;
    private String selectedGender = "Male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "profile_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etPass = findViewById(R.id.password);
        btnUpdate = findViewById(R.id.signup);
        userIv = findViewById(R.id.user_iv);
        progressBar = findViewById(R.id.progress_bar);
        deactivateBt = findViewById(R.id.deactive_bt);
        genderSpinner = findViewById(R.id.gender_spinner);

        id = PreferenceUtils.getUserId(ProfileActivity.this);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmail.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please enter valid email", Toast.LENGTH_LONG).show();
                    return;
                } else if (etName.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please enter name", Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();
                String name = etName.getText().toString();

                updateProfile(id, email, name, pass);

            }
        });

        //gender spinner setup
        final String[] genderArray = new String[2];
        genderArray[0] = "Male";
        genderArray[1] = "Female";
        genderSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Select Gender");
                builder.setSingleChoiceItems(genderArray, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((TextView) v).setText(genderArray[i]);
                        selectedGender = genderArray[i];
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        getProfile();
    }

    @Override
    protected void onStart() {
        super.onStart();

        userIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        deactivateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeactivationDialog();
            }
        });
    }

    private void showDeactivationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_deactivate, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final EditText passEt = view.findViewById(R.id.pass_et);
        final EditText reasonEt = view.findViewById(R.id.reason_et);
        final Button okBt = view.findViewById(R.id.ok_bt);
        Button cancelBt = view.findViewById(R.id.cancel_bt);
        ImageView closeIv = view.findViewById(R.id.close_iv);
        final ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        LinearLayout topLayout = view.findViewById(R.id.top_layout);
        if (isDark) {
            topLayout.setBackgroundColor(getResources().getColor(R.color.overlay_dark_30));
        }

        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passEt.getText().toString();
                String reason = reasonEt.getText().toString();

                if (TextUtils.isEmpty(pass)) {
                    new ToastMsg(ProfileActivity.this).toastIconError("Please enter your password");
                    return;
                } else if (TextUtils.isEmpty(reason)) {
                    new ToastMsg(ProfileActivity.this).toastIconError("Please enter your reason");
                    return;
                }
                deactivateAccount(pass, reason, alertDialog, progressBar);
            }
        });

        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    private void deactivateAccount(String pass, String reason, final AlertDialog alertDialog, final ProgressBar progressBar) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        DeactivateAccountApi api = retrofit.create(DeactivateAccountApi.class);
        Call<ResponseStatus> call = api.deactivateAccount(id, pass, reason, Config.API_KEY);
        call.enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, retrofit2.Response<ResponseStatus> response) {
                if (response.code() == 200) {
                    ResponseStatus resStatus = response.body();
                    if (resStatus.getStatus().equalsIgnoreCase("success")) {
                        logoutUser();
                        new ToastMsg(ProfileActivity.this).toastIconSuccess(resStatus.getData());

                        if (PreferenceUtils.isMandatoryLogin(ProfileActivity.this)) {
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        alertDialog.dismiss();
                        finish();
                    } else {
                        new ToastMsg(ProfileActivity.this).toastIconError(resStatus.getData());
                        alertDialog.dismiss();
                    }

                } else {
                    new ToastMsg(ProfileActivity.this).toastIconError("Something went wrong");
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(ProfileActivity.this).toastIconError("Something went wrong");
                alertDialog.dismiss();
            }
        });
    }

    public void logoutUser() {
        DatabaseHelper databaseHelper = new DatabaseHelper(ProfileActivity.this);
        databaseHelper.deleteAllDownloadData();
        databaseHelper.deleteUserData();
        databaseHelper.deleteAllActiveStatusData();

        SharedPreferences.Editor sp = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
        sp.putBoolean(Constants.USER_LOGIN_STATUS, false);
        sp.apply();
        sp.commit();
        ;
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    userIv.setImageURI(selectedImage);
                    imageUri = selectedImage;
                }
                break;
        }
    }

    private void getProfile() {
        User user = new DatabaseHelper(ProfileActivity.this).getUserData();
        String userName = user.getName();
        String userEmail = user.getEmail();
        String userProfileImage = user.getImageUrl();
        String gender = user.getGender();
        Picasso.get()
                .load(Uri.parse(userProfileImage))
                .placeholder(R.drawable.ic_account_circle_black)
                .error(R.drawable.ic_account_circle_black)
                .into(userIv);

        etName.setText(userName);
        etEmail.setText(userEmail);
        if (gender == null) {
            genderSpinner.setText(R.string.male);
        } else {
            genderSpinner.setText(gender);
            selectedGender = gender;
        }
    }

    private void updateProfile(String idString, String emailString, String nameString, String passString) {
        File file = null;
        RequestBody requestFile = null;
        MultipartBody.Part multipartBody = null;
        try {
            file = FileUtil.from(ProfileActivity.this, imageUri);
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),
                    file);

            multipartBody = MultipartBody.Part.createFormData("photo",
                    file.getName(), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), emailString);
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), idString);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), nameString);
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), passString);
        RequestBody key = RequestBody.create(MediaType.parse("text/plain"), Config.API_KEY);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        ProfileApi api = retrofit.create(ProfileApi.class);
        Call<ResponseStatus> call = api.updateProfile(Config.API_KEY, idString, nameString, emailString, passString, imageUri, selectedGender);
        call.enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Call<ResponseStatus> call, retrofit2.Response<ResponseStatus> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        new ToastMsg(ProfileActivity.this).toastIconSuccess(response.body().getData());
                        progressBar.setVisibility(View.GONE);
                    } else {
                        new ToastMsg(ProfileActivity.this).toastIconError(response.body().getData());
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_wrong));
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseStatus> call, Throwable t) {
                new ToastMsg(ProfileActivity.this).toastIconError(getString(R.string.something_went_wrong));
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
