package org.richardinnocent.propertiestoolkit;

/**
 * Implies when a default value/behaviour should be applied during the {@link Property#get()}
 * method.
 * @since 1.0.0
 * @author RichardInnocent
 */
public enum DefaultCondition {
  /**
   * This applies when there is an error in parsing the text from the {@code Properties} value to
   * the given type.
   */
  PARSE_FAILS,
  /**
   * This applies when the text from the {@code Properties} value is {@code null} or blank (@code
   * "").
   */
  IS_EMPTY,
  /**
   * This applies when the text from the {@code Properties} file is successfully parsed, but fails
   * any of the constraint criteria.
   * @see Property#addConstraint(java.util.function.Predicate)
   */
  IS_INVALID
}
