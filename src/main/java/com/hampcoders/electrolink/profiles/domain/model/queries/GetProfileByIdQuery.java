package com.hampcoders.electrolink.profiles.domain.model.queries;

/**
 * Query object representing the data needed to retrieve a profile by its ID.
 *
 * @param profileId The ID of the profile being queried.
 */
public record GetProfileByIdQuery(Long profileId) {}

