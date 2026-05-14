package com.hampcoders.electrolink.profiles.domain.model.commands;

/**
 * Command object representing the data needed to delete a profile.
 *
 * @param profileId The ID of the profile to be deleted.
 */
public record DeleteProfileCommand(Long profileId) {}
