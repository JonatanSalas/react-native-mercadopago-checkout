package com.blackboxvision.reactnative.mercadopagocheckout;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentData;
import com.mercadopago.util.JsonUtil;

import static android.app.Activity.RESULT_CANCELED;

public final class MercadoPagoCheckoutEventListener implements ActivityEventListener {
    private static final String PAYMENT_CANCELLED = "PAYMENT_CANCELLED";
    private static final String PAYMENT_ERROR = "PAYMENT_ERROR";

    private static final String MP_PAYMENT = "payment";
    private static final String MP_PAYMENT_DATA = "paymentData";
    private static final String MP_PAYMENT_ERROR = "mercadoPagoError";

    private static final String PAYMENT_STATUS = "status";
    private static final String PAYMENT_ID = "id";

    private static final String PAYMENT_DATA_CARD_ISSUER_ID = "cardIssuerId";
    private static final String PAYMENT_DATA_METHOD_ID = "paymentMethodId";
    private static final String PAYMENT_DATA_INSTALLMENT = "installments";
    private static final String PAYMENT_DATA_CAMPAIGN_ID = "campaignId";
    private static final String PAYMENT_DATA_CARD_TOKEN = "cardTokenId";

    private Promise currentPromise;

    public void setCurrentPromise(final Promise promise) {
        currentPromise = promise;
    }

    private void clearCurrentPromise() {
        currentPromise = null;
    }

    private WritableMap paymentToMap(@NonNull Payment payment) {
        final String paymentId = payment.getId().toString();
        final String paymentStatus = payment.getStatus();

        final WritableMap map = Arguments.createMap();

        map.putString(PAYMENT_ID, paymentId);
        map.putString(PAYMENT_STATUS, paymentStatus);

        return map;
    }

    private WritableMap paymentDataToMap(@NonNull PaymentData paymentData) {
        final String paymentMethodId = paymentData.getPaymentMethod().getId();
        final String cardTokenId = paymentData.getToken() == null ? null : paymentData.getToken().getId();
        final String cardIssuerId = paymentData.getIssuer() == null ? null : paymentData.getIssuer().getId().toString();
        final String campaignId = paymentData.getDiscount() == null ? null : paymentData.getDiscount().getId().toString();
        final String installment = paymentData.getPayerCost() == null ? null : paymentData.getPayerCost().getInstallments().toString();

        final WritableMap map = Arguments.createMap();

        map.putString(PAYMENT_DATA_METHOD_ID, paymentMethodId);
        map.putString(PAYMENT_DATA_CARD_TOKEN, cardTokenId);
        map.putString(PAYMENT_DATA_CARD_ISSUER_ID, cardIssuerId);
        map.putString(PAYMENT_DATA_CAMPAIGN_ID, campaignId);
        map.putString(PAYMENT_DATA_INSTALLMENT, installment);

        return map;
    }

    private <T> T getData(@NonNull Intent data, @NonNull String key, @NonNull Class<T> clazz) {
        return JsonUtil.getInstance().fromJson(data.getStringExtra(key), clazz);
    }

    public void onNewIntent(Intent intent) { }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (currentPromise == null || requestCode != MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            return;
        }

        if (resultCode == MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE) {
            final PaymentData paymentData = this.getData(data, MP_PAYMENT_DATA, PaymentData.class);
            final WritableMap paymentDataMap = this.paymentDataToMap(paymentData);

            //Resolve values as map
            currentPromise.resolve(paymentDataMap);
        }
        else if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
            final Payment payment = this.getData(data, MP_PAYMENT, Payment.class);
            final WritableMap paymentMap = this.paymentToMap(payment);

            //Resolve values as map
            currentPromise.resolve(paymentMap);
        }
        else if (resultCode == RESULT_CANCELED) {
            if (data != null && data.getStringExtra(MP_PAYMENT_ERROR) != null) {
                final MercadoPagoError mercadoPagoError = this.getData(data, MP_PAYMENT_ERROR, MercadoPagoError.class);
                final Throwable wrappedError = new Throwable(mercadoPagoError.getErrorDetail());

                //Reject promise on MPError, and bubble error data to react-native
                currentPromise.reject(PAYMENT_ERROR, "Payment failed.", wrappedError);
            }
            else {
                //Reject promise on user cancellation
                currentPromise.reject(PAYMENT_CANCELLED, "Payment was cancelled by the user.");
            }
        }

        this.clearCurrentPromise();
    }
}
