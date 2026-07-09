package com.hampcoders.electrolink.iam.infrastructure.authorization.sfs.services;

import com.hampcoders.electrolink.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AuthenticatedUserService class.
 * This class is used to get the authenticated user's email from the security context.
 * It is used by the application services to get the authenticated user's email.
 */
@Component
public class AuthenticatedUserService {

  /**
   * Get the authenticated user's email from the security context.
   *
   * @return an Optional containing the authenticated user's email if the user is authenticated,
   *     or an empty Optional if the user is not authenticated
   */
  public Optional<String> getAuthenticatedEmail() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return Optional.empty();
    }

    var principal = auth.getPrincipal();

    if (principal instanceof UserDetailsImpl userDetails) {
      return Optional.of(userDetails.getUsername());
    }

    return Optional.empty();
  }
}