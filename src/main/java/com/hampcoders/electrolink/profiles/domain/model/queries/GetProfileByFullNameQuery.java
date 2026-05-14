package com.hampcoders.electrolink.profiles.domain.model.queries;

/**
 * Query object representing the data needed to retrieve a profile by its full name.
 *
 * @param firstName The first name of the profile being queried.
 * @param lastName The last name of the profile being queried.
 */
public record GetProfileByFullNameQuery(String firstName, String lastName) {}

