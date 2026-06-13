package com.hampcoders.electrolink.sdp.interfaces.rest.resources;

/**
 * Resource representing the policy details of a service.
 *
 * @param cancellationPolicy The cancellation policy of the service.
 *
 * @param termsAndConditions The terms and conditions associated with the service.
 */
public record PolicyResource(
        String cancellationPolicy,
        String termsAndConditions
) {}
