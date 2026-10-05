package in.advohq.dto;

/**
 * Everything the browser needs to open Razorpay Checkout. Contains the
 * publishable key id only — the key secret never leaves the server.
 */
public record SubscriptionCheckoutResponse(String razorpayKeyId,
                                           String subscriptionId,
                                           String planCode,
                                           String planName,
                                           long amountPaise,
                                           String customerName,
                                           String customerEmail,
                                           String customerContact) {}
