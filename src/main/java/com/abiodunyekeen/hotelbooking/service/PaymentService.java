package com.abiodunyekeen.hotelbooking.service;


import com.abiodunyekeen.hotelbooking.request.SessionRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Slf4j
@Service
public class PaymentService {


    @Value("${stripeApiKey}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }


public SessionRequest createPaymentSession(SessionRequest  sessionRequest)  {

        try {
            double amount = 2000.00;

            Customer customer = findCustomer("email","name");

            String clientUrl = "http://localhost:5172";
            SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomer(customer.getEmail())
                    .setSuccessUrl(clientUrl + "/success?session_id={CHECKOUT_SESSION_ID")
                    .setCancelUrl(clientUrl + "/failure");

            //items and amount
            builder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .putMetadata("booking_d", "")
                                            .putMetadata("user_id", sessionRequest.getUserId())
                                            .setName("Room Type")
                                            .build()
                                    )
                                    .setCurrency("USD")
                                    .setUnitAmountDecimal(BigDecimal.valueOf(amount * 100))
                                    .build())
                            .build()

            ).build();

            SessionCreateParams.PaymentIntentData paymentIntentData =
                    SessionCreateParams.PaymentIntentData.builder()
                            .putMetadata("booking_d", "")
                            .putMetadata("user_id", sessionRequest.getUserId())
                            .build();

            builder.setPaymentIntentData(paymentIntentData);
            Session session = Session.create(builder.build());
            sessionRequest.setSessionUrl(session.getUrl());
            sessionRequest.setSessionUrl(session.getId());

        }catch (StripeException e){
            log.error("Exception createPaymentSession");
            sessionRequest.setMessage(e.getMessage());
        }

        return sessionRequest;

}

    private Customer findCustomer(String email, String name) throws StripeException {

        CustomerSearchParams params = CustomerSearchParams.builder()
                .setQuery("email:'" + email +"'")
                .build();
        CustomerSearchResult searchResult = Customer.search(params);
        Customer customer;

        if(searchResult.getData().isEmpty()){
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(name)
                    .setEmail(email)
                    .build();
            customer = Customer.create(customerCreateParams);

        }else{
            customer = searchResult.getData().get(0);
        }

        return customer;


    }


}
