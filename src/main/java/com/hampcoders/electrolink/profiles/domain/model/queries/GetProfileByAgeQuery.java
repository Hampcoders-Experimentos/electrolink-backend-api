package com.hampcoders.electrolink.profiles.domain.model.queries;

/**
 * Query object representing the data needed to retrieve profiles by age.
 *
 * @param age The age of the profiles being queried.
 */
public record GetProfileByAgeQuery(int age) {}

