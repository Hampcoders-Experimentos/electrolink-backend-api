package com.hampcoders.electrolink.shared.test.util;

import java.lang.reflect.Field;

/**
 * Minimal reflection helpers for tests.
 *
 * <p>Useful for reading or assigning private fields that have no setter, such as
 * auto-generated identifiers inherited from
 * {@code AuditableAbstractAggregateRoot}.</p>
 */
public final class ReflectionTestUtils {

  private ReflectionTestUtils() {
  }

  /**
   * Assigns a value to a (possibly inherited) private field of the target.
   *
   * @param target    The object whose field will be set.
   * @param fieldName The name of the field.
   * @param value     The value to assign.
   */
  public static void setField(Object target, String fieldName, Object value) {
    Field field = resolveField(target.getClass(), fieldName);
    field.setAccessible(true);
    try {
      field.set(target, value);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Unable to set field '" + fieldName + "'", e);
    }
  }

  /**
   * Reads the value of a (possibly inherited) private field of the target.
   *
   * @param target    The object whose field will be read.
   * @param fieldName The name of the field.
   * @return The current field value.
   */
  public static Object getField(Object target, String fieldName) {
    Field field = resolveField(target.getClass(), fieldName);
    field.setAccessible(true);
    try {
      return field.get(target);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Unable to read field '" + fieldName + "'", e);
    }
  }

  private static Field resolveField(Class<?> type, String fieldName) {
    for (Class<?> current = type; current != null; current = current.getSuperclass()) {
      try {
        return current.getDeclaredField(fieldName);
      } catch (NoSuchFieldException ignored) {
        // continue walking up the hierarchy
      }
    }
    throw new IllegalArgumentException("Field '" + fieldName + "' not found on " + type.getName());
  }
}
