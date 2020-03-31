package com.burmesesubtitles.app.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentConfig {

    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("paypal_email")
    @Expose
    private String paypalEmail;
    @SerializedName("paypal_client_id")
    @Expose
    private String paypalClientId;
    @SerializedName("stripe_publishable_key")
    @Expose
    private String stripePublishableKey;
    @SerializedName("stripe_secret_key")
    @Expose
    private String stripeSecretKey;
    @SerializedName("reve_public_key")
    @Expose
    private String revePublicKey;
    @SerializedName("reve_secret_key")
    @Expose
    private String reveSecretKey;
    @SerializedName("reve_encryption_key")
    @Expose
    private String reveEncryptionKey;
    @SerializedName("play_stack_public_key")
    @Expose
    private String playStackPublicKey;
    @SerializedName("exchnage_rate")
    @Expose
    private String exchangeRate;

    public PaymentConfig() {
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaypalEmail() {
        return paypalEmail;
    }

    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }

    public String getPaypalClientId() {
        return paypalClientId;
    }

    public void setPaypalClientId(String paypalClientId) {
        this.paypalClientId = paypalClientId;
    }

    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public void setStripePublishableKey(String stripePublishableKey) {
        this.stripePublishableKey = stripePublishableKey;
    }

    public String getStripeSecretKey() {
        return stripeSecretKey;
    }

    public void setStripeSecretKey(String stripeSecretKey) {
        this.stripeSecretKey = stripeSecretKey;
    }

    public String getRevePublicKey() {
        return revePublicKey;
    }

    public void setRevePublicKey(String revePublicKey) {
        this.revePublicKey = revePublicKey;
    }

    public String getReveSecretKey() {
        return reveSecretKey;
    }

    public void setReveSecretKey(String reveSecretKey) {
        this.reveSecretKey = reveSecretKey;
    }

    public String getReveEncryptionKey() {
        return reveEncryptionKey;
    }

    public void setReveEncryptionKey(String reveEncryptionKey) {
        this.reveEncryptionKey = reveEncryptionKey;
    }

    public String getPlayStackPublicKey() {
        return playStackPublicKey;
    }

    public void setPlayStackPublicKey(String playStackPublicKey) {
        this.playStackPublicKey = playStackPublicKey;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
