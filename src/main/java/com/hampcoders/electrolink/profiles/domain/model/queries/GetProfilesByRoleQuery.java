package com.hampcoders.electrolink.profiles.domain.model.queries;

import com.hampcoders.electrolink.profiles.domain.model.valueobjects.Role;

/**
 * Query object representing the data needed to retrieve profiles based on their role.
 *
 * @param role The role for which profiles are being queried.
 */
public record GetProfilesByRoleQuery(Role role) {}
