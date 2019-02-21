package com.richardinnocent.propertiestoolkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * The default settings express the default return value and optional behaviour that should be
 * applied when encountering specified {@link DefaultCondition}s.
 * @param <T> The type for the return value.
 * @author RichardInnocent
 */
public class DefaultSettings<T> {

  private final Map<DefaultCondition, ReturnBehaviour> behaviourMap = new HashMap<>(1);

  /**
   * Creates a new {@code Setting} object which contains the expected behaviour for these specified
   * conditions. As a result, it's possible to specify different behaviour for different
   * conditions, e.g.:<br>
   * <pre>
   * DefaultSettings<Integer> settings = new DefaultSettings<>()
   *   .when(DefaultCondition.IS_EMPTY, DefaultCondition.PARSE_FAILS)
   *     .thenReturn(0)
   *   .when(DefaultCondition.IS_INVALID)
   *     .thenDo((key, value) ->
   *         LOGGER.warn("Value for key, " + key + " is invalid: " + value))
   *     .thenReturn(10);</pre>
   * This behaviour is only applied if the {@link Setting#thenReturn(Object)} method is
   * subsequently called.
   *
   * <pre>
   * // Nothing happens
   * new DefaultSettings<Integer>().when(DefaultCondition.IS_EMPTY);
   *
   * // Nothing happens
   * new DefaultSettings<Integer>().when(DefaultCondition.IS_EMPTY)
   *                      .thenDo(key, value -> LOGGER.debug(Key + ": " + value));
   *
   * // Successfully added to the settings
   * new DefaultSettings<Integer>().when(DefaultCondition.IS_EMPTY)
   *                      .thenReturn(0d);</pre>
   *
   * Note that, if the same {@code DefaultCondition} is specified multiple times, the most recent
   * {@code Setting} that was applied will dictate the behaviour if this condition is met.
   * @param conditions The conditions for which the soon-to-be-specified task (if appropriate) and
   *   return value should be applied.
   * @return
   */
  public Setting when(DefaultCondition... conditions) {
    return new Setting(conditions);
  }

  /**
   * Checks to see if the behaviour for this condition has been specified in {@code this} settings
   * instance. If it has, the task is executed, if present, and then the default value is returned.
   * If the behaviour for this condition has not been expressed, the exception, {@code e}, is
   * thrown.
   * @param condition The condition that has been triggered.
   * @param key The key to pass to the task, if appropriate.
   * @param value The value, in its raw {@code String} form, that can be passed to the task, if
   *   appropriate.
   * @param e The exception to throw if behaviour for this condition has not been defined.
   * @return The appropriate return value for this condition.
   * @throws PropertiesException Thrown if behaviour for this condition has not been defined.
   */
  T apply(DefaultCondition condition, String key, String value, PropertiesException e)
      throws PropertiesException {
    ReturnBehaviour behaviour = behaviourMap.get(condition);
    if (behaviour == null)
      throw e;

    if (behaviour.task != null)
      behaviour.task.accept(key, value);

    return behaviour.returnValue;
  }

  private void saveBehaviourMap(Setting setting) {
    setting.getConditions()
           .forEach(condition ->
             behaviourMap.put(condition,
                              new ReturnBehaviour(setting.getTask(), setting.getReturnValue())));
  }

  /**
   * Object to contain the behaviour for specified {@link DefaultCondition}s.
   */
  public class Setting {

    private final Set<DefaultCondition> conditions = new HashSet<>(1);
    private BiConsumer<String, String> task;
    private T returnValue;

    Setting(DefaultCondition... conditions) {
      if (conditions == null || conditions.length < 1)
        throw new IllegalArgumentException("Conditions cannot be null or empty");

      Stream.of(conditions)
            .filter(condition -> condition != null)
            .forEach(this.conditions::add);
    }

    Set<DefaultCondition> getConditions() {
      return conditions;
    }

    BiConsumer<String, String> getTask() {
      return task;
    }

    T getReturnValue() {
      return returnValue;
    }

    /**
     * Sets the task to execute when the specific condition is met, before the return value is
     * returned. This might be to log a warning to a {@code Logger}, for example. Only one task may
     * be specified.
     * @param task The task to complete. This is a consumer that takes two {@code String}s: first
     *   the key name, then the raw {@code String} value received from the {@code Properties} file.
     * @return {@code this} settings object, for chaining.
     */
    public Setting thenDo(BiConsumer<String, String> task) {
      this.task = task;
      return this;
    }

    /**
     * Sets the value that should be returned, in the event that any of the conditions are met.
     * This method is a required call, in order to add this {@code Setting} to the {@code
     * DefaultSettings} instance.
     * @param returnValue The value that should be returned.
     * @return {@code this} {@code DefaultSettings} object, so that additional cases can be
     *   appended.
     */
    public DefaultSettings<T> thenReturn(T returnValue) {
      this.returnValue = returnValue;
      DefaultSettings.this.saveBehaviourMap(this);
      return DefaultSettings.this;
    }

  }

  private class ReturnBehaviour {
    private final BiConsumer<String, String> task;
    private final T returnValue;

    ReturnBehaviour(BiConsumer<String, String> task, T returnValue) {
      this.task = task;
      this.returnValue = returnValue;
    }
  }

}
