package com.hampcoders.electrolink.profiles.domain.model.queries;

/**
 * Query object representing the data needed to retrieve a profile by its email address.
 *
 * @param email The email address of the profile being queried.
 */
public record GetProfileByEmailQuery(String email) {}
