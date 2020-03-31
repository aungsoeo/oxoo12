package com.burmesesubtitles.app;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.burmesesubtitles.app.database.DatabaseHelper;
import com.burmesesubtitles.app.network.RetrofitClient;
import com.burmesesubtitles.app.network.apis.SignUpApi;
import com.burmesesubtitles.app.network.apis.SubscriptionApi;
import com.burmesesubtitles.app.network.model.ActiveStatus;
import com.burmesesubtitles.app.network.model.User;
import com.burmesesubtitles.app.utils.Constants;
import com.burmesesubtitles.app.utils.ToastMsg;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName,etEmail,etPass;
    private Button btnSignup;
    private ProgressDialog dialog;
    private View backgorundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SignUp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "sign_up_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        etName=findViewById(R.id.name);
        etEmail=findViewById(R.id.email);
        etPass=findViewById(R.id.password);
        btnSignup=findViewById(R.id.signup);
        backgorundView=findViewById(R.id.background_view);
        if (isDark) {
            backgorundView.setBackgroundColor(getResources().getColor(R.color.nav_head_bg));
            btnSignup.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));
        }
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidEmailAddress(etEmail.getText().toString())){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter valid email");
                }else if(etPass.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter password");
                }else if (etName.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter name");
                }else {
                    String email = etEmail.getText().toString();
                    String pass = etPass.getText().toString();
                    String name = etName.getText().toString();
                    signUp(email, pass, name);
                }
            }
        });
    }

    private void signUp(String email, String pass, String name){
        dialog.show();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SignUpApi signUpApi = retrofit.create(SignUpApi.class);
        Call<User> call = signUpApi.signUp(Config.API_KEY, email, pass, name);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();

                if (user.getStatus().equals("success")) {
                    new ToastMsg(SignUpActivity.this).toastIconSuccess("Successfully registered");

                    // save user info to sharedPref
                    saveUserInfo(user, user.getName(), etEmail.getText().toString(),
                            user.getUserId());

                } else if (user.getStatus().equals("error")){
                    new ToastMsg(SignUpActivity.this).toastIconError(user.getData());
                    dialog.cancel();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                new ToastMsg(SignUpActivity.this).toastIconError("Something went wrong."+ t.getMessage());
                t.printStackTrace();
                dialog.cancel();

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

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public void saveUserInfo(User user, String name, String email, String id) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.USER_LOGIN_STATUS, MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("id", id);
        editor.putBoolean("status",true);
        editor.putBoolean(Constants.USER_LOGIN_STATUS, true);
        editor.apply();

        DatabaseHelper db = new DatabaseHelper(SignUpActivity.this);
        db.deleteUserData();
        db.insertUserData(user);
        //save user login time, expire time
        updateSubscriptionStatus(user.getUserId());

    }

    private void updateSubscriptionStatus(String userId) {
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubscriptionApi subscriptionApi = retrofit.create(SubscriptionApi.class);

        Call<ActiveStatus> call = subscriptionApi.getActiveStatus(Config.API_KEY, userId);
        call.enqueue(new Callback<ActiveStatus>() {
            @Override
            public void onResponse(Call<ActiveStatus> call, Response<ActiveStatus> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        ActiveStatus activeStatus = response.body();

                        DatabaseHelper db = new DatabaseHelper(SignUpActivity.this);
                        db.deleteAllActiveStatusData();
                        db.insertActiveStatusData(activeStatus);

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        startActivity(intent);
                        finish();
                        dialog.cancel();
                    }
                }
            }
            @Override
            public void onFailure(Call<ActiveStatus> call, Throwable t) {
                t.printStackTrace();
                new ToastMsg(SignUpActivity.this).toastIconError(getResources().getString(R.string.something_went_wrong));
            }
        });
    }


}
