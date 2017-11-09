import React, { Component } from 'react';
import { StyleSheet, Text, View, Button } from 'react-native';
import { MercadoPagoCheckout } from 'react-native-mercadopago-checkout';

export default class Example extends Component {
    static defaultProps = {
        publicKey: "TEST-ad365c37-8012-4014-84f5-6c895b3f8e0a",
        preferenceId: "150216849-ceed1ee4-8ab9-4449-869f-f4a8565d386f",
        enableDarkFont: false,
        background: {
            payment: '#673AB7',
            paymentData: '#3F51B5'
        }
    };

    state = {
        status: 'No Action applied yet',
        error: 'No error captured yet'
    };

    handlePaymentClick = async _ => {
        try {
            this.setState(prevState => ({
                status: 'No Action applied yet',
                error: 'No error captured yet'
            }));

            const payment = await MercadoPagoCheckout.startForPayment(this.props.publicKey, this.props.preferenceId, {
                backgroundColor: this.props.background.payment,
                enableDarkFont: this.props.enableDarkFont
            });

            this.setState(prevState => ({
                status: JSON.stringify(payment, null, 2)
            }));
        } catch (error) {
            this.setState(prevState => ({
                error: error.message
            }));
        }
    };

    handlePaymentDataClick = async _ => {
        try {
            this.setState(prevState => ({
                status: 'No Action applied yet',
                error: 'No error captured yet'
            }));

            const payment = await MercadoPagoCheckout.startForPaymentData(this.props.publicKey, this.props.preferenceId, {
                backgroundColor: this.props.background.paymentData,
                enableDarkFont: this.props.enableDarkFont
            });

            this.setState(prevState => ({
                status: JSON.stringify(payment, null, 2)
            }));
        } catch (error) {
            this.setState(prevState => ({
                error: error.message
            }));
        }
    };

    render() {
        return (
            <View style={styles.container}>
                <Text>
                    You're going to checkout a product with $ 100,00 of cost.
                </Text>
                <Button
                    title="Checkout For Payment"
                    onPress={this.handlePaymentClick}
                    color={this.props.background.payment}
                />
                <Button
                    title="Checkout For Payment Data"
                    onPress={this.handlePaymentDataClick}
                    color={this.props.background.paymentData}
                />
                <Text style={styles.statusText}>
                    Current Status: {this.state.status}
                </Text>
                <Text style={styles.errorText}>
                    Has Error: {this.state.error}
                </Text>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF'
    },
    statusText: {
        padding: 8,
        marginTop: 4,
        color: '#FFFFFF',
        backgroundColor: '#1B5E20'
    },
    errorText: {
        padding: 8,
        marginTop: 4,
        color: '#FFFFFF',
        backgroundColor: '#B71C1C'
    }
});
