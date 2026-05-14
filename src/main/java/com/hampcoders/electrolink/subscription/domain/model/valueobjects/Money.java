package com.hampcoders.electrolink.subscription.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Represents a monetary value with an amount and a currency.
 */
@Embeddable
public record Money(BigDecimal amount, Currency currency) {

  /**
   * Creates a new Money instance with the specified amount and currency.
   *
   * @param amount the monetary amount, must not be null and must be non-negative
   * @param currency the currency, must not be null
   */
  public Money {
    Objects.requireNonNull(amount, "amount must not be null");
    Objects.requireNonNull(currency, "currency must not be null");
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("amount must not be negative");
    }
  }

  /**
   * Creates a new Money instance with a default amount of zero and the default currency (USD).
   */
  public Money() {
    this(BigDecimal.ZERO, Currency.getInstance("USD"));
  }

  /**
   * Factory method to create a Money instance with the specified amount in USD.
   *
   * @param amount the monetary amount in USD, must be non-negative
   * @return a new Money instance with the specified amount in USD
   */
  public static Money usd(double amount) {
    return new Money(BigDecimal.valueOf(amount), Currency.getInstance("USD"));
  }
}
