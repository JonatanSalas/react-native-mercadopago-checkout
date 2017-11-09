#import "CheckoutMercadoPagoModule.h"
#import "AppDelegate.h"

@import MercadoPagoSDK;

@implementation MercadoPagoCheckoutModule

RCT_EXPORT_MODULE()

- (dispatch_queue_t) methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_METHOD(startCheckoutForPayment: (NSString *) publicKey: (NSString *) preferenceId: (NSString *) color: (BOOL) enableDarkFont: (RCTPromiseResolveBlock) resolve: (RCTPromiseRejectBlock) reject) {
    //Get UINavigationController from AppDelegate
    AppDelegate *share = (AppDelegate *)[UIApplication sharedApplication].delegate;
    UINavigationController *uiNavigationController = (UINavigationController *) share.window.rootViewController;

    //Set DecorationPreference on MercadoPagoCheckout
    DecorationPreference *decorationPreference = [[DecorationPreference alloc] initWithBaseColor:[UIColor fromHex:color]];
    [MercadoPagoCheckout setDecorationPreference: decorationPreference];

    //Set CheckoutPreference on MercadoPagoCheckout
    CheckoutPreference *preference = [[CheckoutPreference alloc] initWith_id:preferenceId];
    MercadoPagoCheckout *checkout = [[MercadoPagoCheckout alloc] initWithPublicKey:publicKey checkoutPreference:preference discount:nil navigationController:uiNavigationController];

    //Set up Cancellation Callback
    [checkout setCallbackCancelWithCallback:^{
        [uiNavigationController setNavigationBarHidden:TRUE];
        [uiNavigationController popToRootViewControllerAnimated:NO];

        reject(@"PAYMENT_CANCELLED", @"Payment was cancelled by the user.", nil);
    }];

    //Set up Payment Callback
    [MercadoPagoCheckout setPaymentCallbackWithPaymentCallback:^(Payment * payment) {
        if (payment != nil) {
            NSDictionary *paymentDictionary = @{@"id": payment._id, @"status": payment.status};

            resolve(paymentDictionary);
        } else {
            NSLog(@"PaymentResult is NIL.");
        }

        [uiNavigationController setNavigationBarHidden:TRUE];
        [uiNavigationController popToRootViewControllerAnimated:NO];
    }];

    [checkout start];

    [uiNavigationController setNavigationBarHidden:FALSE];
}

RCT_EXPORT_METHOD(startCheckoutForPaymentData: (NSString *) publicKey: (NSString *) preferenceId: (NSString *) color: (BOOL) enableDarkFont: (RCTPromiseResolveBlock) resolve: (RCTPromiseRejectBlock) reject) {
    //Get UINavigationController from AppDelegate
    AppDelegate *share = (AppDelegate *)[UIApplication sharedApplication].delegate;
    UINavigationController *uiNavigationController = (UINavigationController *) share.window.rootViewController;

    //Set DecorationPreference on MercadoPagoCheckout
    DecorationPreference *decorationPreference = [[DecorationPreference alloc] initWithBaseColor:[UIColor fromHex:color]];
    [MercadoPagoCheckout setDecorationPreference: decorationPreference];

    //Set CheckoutPreference on MercadoPagoCheckout
    CheckoutPreference *preference = [[CheckoutPreference alloc] initWith_id:preferenceId];
    MercadoPagoCheckout *checkout = [[MercadoPagoCheckout alloc] initWithPublicKey:publicKey checkoutPreference:preference discount:nil navigationController:uiNavigationController];

    //Set up Cancellation Callback
    [checkout setCallbackCancelWithCallback:^{
        [uiNavigationController setNavigationBarHidden:TRUE];
        [uiNavigationController popToRootViewControllerAnimated:NO];

        reject(@"PAYMENT_CANCELLED", @"Payment was cancelled by the user.", nil);
    }];

    //Set up PaymentData Callback
    [MercadoPagoCheckout setPaymentDataConfirmCallbackWithPaymentDataConfirmCallback:^(PaymentData * paymentData) {
        if (paymentData != nil) {
            NSString *campaignId = paymentData.discount._id != nil ? paymentData.discount._id : @"";
            NSString *installments = [@(paymentData.payerCost.installments) stringValue];
            NSString *paymentMethodId = paymentData.paymentMethod._id;
            NSString *cardIssuerId = paymentData.issuer._id;
             NSString *cardTokenId = paymentData.token._id;

            NSDictionary *paymentDataDictionary = @{@"paymentMethodId": paymentMethodId, @"cardTokenId": cardTokenId, @"cardIssuerId": cardIssuerId, @"installments": installments, @"campaignId": campaignId };

            resolve(paymentDataDictionary);
        } else {
            NSLog(@"PaymentData is NIL.");
        }

        [uiNavigationController setNavigationBarHidden:TRUE];
        [uiNavigationController popToRootViewControllerAnimated:NO];
    }];

    [checkout start];

    [uiNavigationController setNavigationBarHidden:FALSE];
}

@end
