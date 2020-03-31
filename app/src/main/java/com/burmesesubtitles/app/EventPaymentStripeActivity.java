package com.burmesesubtitles.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.burmesesubtitles.app.database.DatabaseHelper;
import com.burmesesubtitles.app.models.Event;
import com.burmesesubtitles.app.network.RetrofitClient;
import com.burmesesubtitles.app.network.apis.PaymentApi;
import com.burmesesubtitles.app.network.model.PaymentConfig;
import com.burmesesubtitles.app.utils.PreferenceUtils;
import com.burmesesubtitles.app.utils.ApiResources;
import com.burmesesubtitles.app.utils.ToastMsg;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EventPaymentStripeActivity extends AppCompatActivity {
    private Event event;

    private Toolbar mToolbar;
    private TextInputEditText mCardNoEt, mValidDateEt, mCvvNoEt, mCardNameEt;
    private Button mSubmitBt;
    private ProgressBar progressBar;
    private CardInputWidget cardInputWidget;

    private Calendar myCalendar = Calendar.getInstance();

    private int month, year;
    private String userId;

    private String secretKey, publisherKey;

    private boolean isDark;
    private DatabaseHelper databaseHelper;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        setContentView(R.layout.activity_event_payment_stripe);
        databaseHelper = new DatabaseHelper(EventPaymentStripeActivity.this);

        event = (Event) getIntent().getSerializableExtra("event");


        intiView();


        userId = PreferenceUtils.getUserId(EventPaymentStripeActivity.this);

        PaymentConfig paymentConfig = databaseHelper.getConfigurationData().getPaymentConfig();
        secretKey = paymentConfig.getStripeSecretKey();
        publisherKey = paymentConfig.getStripePublishableKey();

        if (isDark) {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.dark));
            mSubmitBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));
            cardInputWidget.setBackground(getResources().getDrawable(R.drawable.rounded_black_transparent));
        }

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Payment For \""+ event.getEventName()+"\"");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final DatePickerDialog.OnDateSetListener date = new
                DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }

                };

        mValidDateEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(EventPaymentStripeActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return true;
            }
        });
    }

    private void updateLabel() {

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);

        String myFormat = "MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mValidDateEt.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSubmitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CardInputWidget mCardInputWidget = findViewById(R.id.card_iw);
                Card card = mCardInputWidget.getCard();
                if (card != null) {
                    if (!card.validateCard()) {
                        new ToastMsg(EventPaymentStripeActivity.this).toastIconError(getResources().getString(R.string.invalid_card));
                        return;
                    }
                } else {
                    new ToastMsg(EventPaymentStripeActivity.this).toastIconError(getResources().getString(R.string.invalid_card));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                mSubmitBt.setVisibility(View.GONE);
                Stripe stripe = new Stripe(EventPaymentStripeActivity.this, publisherKey);

                stripe.createToken(
                        card, new ApiResultCallback<Token>() {
                            @Override
                            public void onSuccess(@NonNull Token result) {
                                createCharge(result.getId());
                            }

                            @Override
                            public void onError(@NonNull Exception e) {
                                Toast.makeText(EventPaymentStripeActivity.this, e.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                );

            }
        });

    }

    private void createCharge(final String id) {

        com.stripe.Stripe.apiKey = secretKey;

        // Toast.makeText(this, "hello", Toast.LENGTH_LONG).show();
        Double amount = (Double.valueOf(event.getPrice()) * 100);
        final Map<String, Object> params = new HashMap<>();
        params.put("amount", String.valueOf(amount.intValue()));
        params.put("currency", ApiResources.CURRENCY);
        params.put("description", event.getEventName());
        params.put("source", id);

        //new ChargeAsyncTask(params).execute();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Charge charge = null;
                    try {
                        charge = Charge.create(params);
                        Log.d("EventPayment", charge.toJson());

                        // save the data to the server
                        saveChargeData(charge, id);

                        //Toast.makeText(StripePaymentActivity.this, "hello"+ charge.toJson(), Toast.LENGTH_SHORT).show();
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    } catch (InvalidRequestException e) {
                        e.printStackTrace();
                    } catch (APIConnectionException e) {
                        e.printStackTrace();
                    } catch (CardException e) {
                        e.printStackTrace();
                    } catch (APIException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    private void intiView() {

        mToolbar = findViewById(R.id.payment_toolbar);
        mCardNoEt = findViewById(R.id.card_no_et);
        mCardNameEt = findViewById(R.id.card_name_et);
        mValidDateEt = findViewById(R.id.valid_date_et);
        mCvvNoEt = findViewById(R.id.cvv_no_et);
        mSubmitBt = findViewById(R.id.submit_bt);
        progressBar = findViewById(R.id.progress_bar);
        cardInputWidget = findViewById(R.id.card_iw);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class ChargeAsyncTask extends AsyncTask<Void, Void, Void> {

        private Charge charge;
        private Map<String, Object> params;

        public ChargeAsyncTask(Map<String, Object> params) {
            this.params = params;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                charge = Charge.create(params);
                Log.d("alcjiajf", charge.toJson());
             } catch (AuthenticationException | InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (CardException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            saveChargeData(charge, params.get("source").toString());

        }
    }
    public void saveChargeData(Charge charge, String token) {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call = paymentApi.saveEventPayment(Config.API_KEY, event.getEventId(), userId, String.valueOf(charge.getAmount()),
                token, "Stripe");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    //
                    PreferenceUtils.updateSubscriptionStatus(EventPaymentStripeActivity.this);
                    progressBar.setVisibility(View.GONE);
                    new ToastMsg(EventPaymentStripeActivity.this).toastIconSuccess(getResources().getString(R.string.payment_success));
                    finish();
                } else {
                    new ToastMsg(EventPaymentStripeActivity.this).toastIconError("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }
}
