package com.hampcoders.electrolink.sdp.interfaces.rest.resource;

public record BillResource(
    String billingPeriod,
    double energyConsumed,
    double amountPaid,
    String billImageUrl
) {}
