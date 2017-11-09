package com.blackboxvision.reactnative.mercadopagocheckout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.DecorationPreference;

public final class MercadoPagoCheckoutModule extends ReactContextBaseJavaModule {
    private final MercadoPagoCheckoutEventListener eventResultListener = new MercadoPagoCheckoutEventListener();

    public MercadoPagoCheckoutModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(eventResultListener);
    }

    @Override
    public String getName() {
        return "MercadoPagoCheckoutModule";
    }

    public void onNewIntent(Intent intent) { }

    @ReactMethod
    public void startCheckoutForPayment(@NonNull String publicKey, @NonNull String checkoutPreferenceId, @NonNull String hexColor, @NonNull Boolean enableDarkFont, @NonNull Promise promise) {
        setCurrentPromise(promise);

        //Create a decoration preference
        final DecorationPreference decorationPreference = createDecorationPreference(hexColor, enableDarkFont);
        final CheckoutPreference checkoutPreference = new CheckoutPreference(checkoutPreferenceId);
        final Activity currentActivity = getCurrentActivity();

        new MercadoPagoCheckout.Builder()
                .setDecorationPreference(decorationPreference)
                .setCheckoutPreference(checkoutPreference)
                .setActivity(currentActivity)
                .setPublicKey(publicKey)
                .startForPayment();

        setStatusBarColor(hexColor);
    }

    @ReactMethod
    public void startCheckoutForPaymentData(@NonNull String publicKey, @NonNull String checkoutPreferenceId, @NonNull String hexColor, @NonNull Boolean enableDarkFont, @NonNull Promise promise) {
        setCurrentPromise(promise);

        //Create a decoration preference
        final DecorationPreference decorationPreference = createDecorationPreference(hexColor, enableDarkFont);
        final CheckoutPreference checkoutPreference = new CheckoutPreference(checkoutPreferenceId);
        final Activity currentActivity = getCurrentActivity();

        new MercadoPagoCheckout.Builder()
                .setDecorationPreference(decorationPreference)
                .setCheckoutPreference(checkoutPreference)
                .setActivity(currentActivity)
                .setPublicKey(publicKey)
                .startForPaymentData();

        setStatusBarColor(hexColor);
    }

    private DecorationPreference createDecorationPreference(@NonNull String color, @NonNull Boolean enableDarkFont) {
        final DecorationPreference.Builder preferenceBuilder = new DecorationPreference.Builder().setBaseColor(color);

        if (enableDarkFont) {
            preferenceBuilder.enableDarkFont();
        }

        return preferenceBuilder.build();
    }

    private void setCurrentPromise(@NonNull Promise promise) {
        eventResultListener.setCurrentPromise(promise);
    }

    private void setStatusBarColor(@NonNull final String hexColor) {
        final Activity activity = getCurrentActivity();

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        final Window window = getCurrentActivity().getWindow();

                        if (window != null) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.parseColor(hexColor));
                        }
                    }
                }
            });
        }
    }
}
