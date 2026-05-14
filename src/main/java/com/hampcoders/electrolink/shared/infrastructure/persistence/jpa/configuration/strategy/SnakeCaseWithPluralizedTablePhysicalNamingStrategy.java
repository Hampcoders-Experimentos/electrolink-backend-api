package com.hampcoders.electrolink.shared.infrastructure.persistence.jpa.configuration.strategy;

import static io.github.encryptorcode.pluralize.Pluralize.pluralize;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * This class implements a custom PhysicalNamingStrategy for Hibernate
 * that converts entity names to snake_case and pluralizes table names.
 */
public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy implements PhysicalNamingStrategy {

  /**
   * Converts the given identifier to snake_case for catalog names.
   *
   * @param identifier the original identifier
   * @param jdbcEnvironment the JDBC environment
   * @return the modified identifier in snake_case
   */
  @Override
  public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
    return this.toSnakeCase(identifier);
  }

  /**
   * Converts the given identifier to snake_case for schema names.
   *
   * @param identifier the original identifier
   * @param jdbcEnvironment the JDBC environment
   * @return the modified identifier in snake_case
   */
  @Override
  public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
    return this.toSnakeCase(identifier);
  }

  /**
   * Converts the given identifier to snake_case and pluralizes it for table names.
   *
   * @param identifier the original identifier
   * @param jdbcEnvironment the JDBC environment
   * @return the modified identifier in snake_case and pluralized
   */
  @Override
  public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {

    return this.toSnakeCase(this.toPlural(identifier));
  }

  /**
   * Converts the given identifier to snake_case for sequence names.
   *
   * @param identifier the original identifier
   * @param jdbcEnvironment the JDBC environment
   * @return the modified identifier in snake_case
   */
  @Override
  public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
    return this.toSnakeCase(identifier);
  }

  /**
   * Converts the given identifier to snake_case for column names.
   *
   * @param identifier the original identifier
   * @param jdbcEnvironment the JDBC environment
   * @return the modified identifier in snake_case
   */
  @Override
  public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
    return this.toSnakeCase(identifier);
  }

  /**
   * Helper method to convert an identifier to snake_case.
   *
   * @param identifier the original identifier
   * @return the modified identifier in snake_case
   */
  private Identifier toSnakeCase(final Identifier identifier) {
    if (identifier == null) {
      return null;
    }
    final String regex = "([a-z])([A-Z])";
    final String replacement = "$1_$2";
    final String newName = identifier.getText()
        .replaceAll(regex, replacement)
        .toLowerCase();
    return Identifier.toIdentifier(newName);
  }

  /**
   * Helper method to pluralize an identifier.
   *
   * @param identifier the original identifier
   * @return the modified identifier in plural form
   */
  private Identifier toPlural(final Identifier identifier) {
    final String newName = pluralize(identifier.getText());
    return Identifier.toIdentifier(newName);
  }
}