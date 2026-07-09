package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

/**
 * Resource class representing the billing information associated with a service request.
 *
 * @param billingPeriod the billing period for which the bill is generated
 * @param energyConsumed the amount of energy consumed during the billing period
 * @param amountPaid the total amount paid for the bill, in the relevant currency
 * @param billImageUrl the URL of an image of the bill, if available
 */
public record BillResource(
    String billingPeriod,
    double energyConsumed,
    double amountPaid,
    String billImageUrl
) {}
