package com.hampcoders.electrolink.iam.domain.model.queries;

import com.hampcoders.electrolink.iam.domain.model.valueobjects.Roles;

/**
 * Query to retrieve a specific role by its unique name.
 *
 * @param name The name of the role to retrieve (e.g., ROLE_CLIENT).
 */
public record GetRoleByNameQuery(Roles name) {
}