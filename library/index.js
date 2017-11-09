import { NativeModules } from 'react-native';

const { MercadoPagoCheckoutModule } = NativeModules;

const defaultOptions = {
    backgroundColor: "#009EE3",
    enableDarkFont: false
};

export class MercadoPagoCheckout {

    /**
     * This function starts MercadoPago checkout to get a PaymentResult object, so you don't create the Payment in your servers.
     *
     * @param publicKey - MercadoPago API public Key
     * @param preferenceId - MercadoPago Items preference id
     * @param options - An Object containing properties like: backgroundColor, enableDarkFont
     * @returns {Promise.<*>} - Promise that if resolves gives a PaymentResult object
     */
    static async startForPayment(publicKey, preferenceId, options) {
        const params = { ...defaultOptions, ...options, publicKey, preferenceId };

        MercadoPagoCheckout._checkParams(params);

        return await MercadoPagoCheckoutModule.startCheckoutForPayment(publicKey, preferenceId, params.backgroundColor, params.enableDarkFont);
    }

    /**
     * This function starts MercadoPago checkout to get a PaymentData object, so you can create the Payment in your servers.
     *
     * @param publicKey - MercadoPago API public Key
     * @param preferenceId - MercadoPago Items preference id
     * @param options - An Object containing properties like: backgroundColor, enableDarkFont
     * @returns {Promise.<*>} - Promise that if resolves gives a PaymentData object
     */
    static async startForPaymentData(publicKey, preferenceId, options) {
        const params = { ...defaultOptions, ...options, publicKey, preferenceId };

        MercadoPagoCheckout._checkParams(params);

        return await MercadoPagoCheckoutModule.startCheckoutForPaymentData(publicKey, preferenceId, params.backgroundColor, params.enableDarkFont);
    }

    static _validate(key, value) {
        if (typeof value === 'undefined') {
            throw `${key} required to start MercadoPago checkout`;
        }
    }

    static _checkParams(params) {
        Object.keys(params).forEach(key => params.hasOwnProperty(key) && MercadoPagoCheckout._validate(key, params[key]));
    }
}

