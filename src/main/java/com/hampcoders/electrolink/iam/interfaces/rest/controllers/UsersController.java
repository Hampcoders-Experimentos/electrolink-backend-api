package com.hampcoders.electrolink.iam.interfaces.rest.controllers;

import com.hampcoders.electrolink.iam.domain.model.queries.GetAllUsersQuery;
import com.hampcoders.electrolink.iam.domain.model.queries.GetUserByIdQuery;
import com.hampcoders.electrolink.iam.domain.services.UserQueryService;
import com.hampcoders.electrolink.iam.interfaces.rest.resources.UserResource;
import com.hampcoders.electrolink.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is a REST controller that exposes the user's resource.
 * It includes the following operations:
 * - GET /api/v1/users: returns all the users
 * - GET /api/v1/users/{userId}: returns the user with the given id
 **/
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "User Management Endpoints")
public class UsersController {

  private final UserQueryService userQueryService;

  /**
   * Users Controller Constructor.
   *
   * @param userQueryService the user query service to handle user queries.
   */
  public UsersController(UserQueryService userQueryService) {
    this.userQueryService = userQueryService;
  }

  /**
   * Returns all users.
   *
   * @return a list of user resources.
   * @see UserResource
   */
  @GetMapping
  public ResponseEntity<List<UserResource>> getAllUsers() {
    var getAllUsersQuery = new GetAllUsersQuery();
    var users = userQueryService.handle(getAllUsersQuery);
    var userResources = users.stream()
        .map(UserResourceFromEntityAssembler::toResourceFromEntity)
        .toList();
    return ResponseEntity.ok(userResources);
  }

  /**
   * Returns the user with the given ID.
   *
   * @param userId the user id.
   * @return the user resource with the given id
   * @throws RuntimeException if the user is not found
   * @see UserResource
   */
  @GetMapping(value = "/{userId}")
  public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
    var getUserByIdQuery = new GetUserByIdQuery(userId);
    var user = userQueryService.handle(getUserByIdQuery);
    if (user.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
    return ResponseEntity.ok(userResource);
  }
}