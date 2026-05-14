package com.hampcoders.electrolink.iam.infrastructure.authorization.sfs.services;

import com.hampcoders.electrolink.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.hampcoders.electrolink.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for providing the user details to the Spring Security framework.
 * It implements the UserDetailsService interface.
 */
@Service(value = "defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * User Details Service Implementation constructor.
   *
   * @param userRepository The User Repository to access the user data from the database.
   *     It is injected by Spring's dependency injection mechanism.
   *     The UserRepository is responsible for performing CRUD operations on the User entity.
   *     It provides methods to find a user by username, save a user, delete a user, etc.
   */
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * This method is responsible for loading the user details from the database.
   *
   * @param username The username.
   * @return The UserDetails object.
   * @throws UsernameNotFoundException If the user is not found.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(
            "User not found with username: " + username));
    return UserDetailsImpl.build(user);
  }
}