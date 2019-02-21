package com.richardinnocent.propertiestoolkit;

/**
 * Implies when a default value/behaviour should be applied during the {@link Property#get()}
 * method.
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
   * any of the validation criteria.
   * @see Property#addValidation(java.util.function.Predicate)
   */
  IS_INVALID
}
