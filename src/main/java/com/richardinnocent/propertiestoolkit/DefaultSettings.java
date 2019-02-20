package com.richardinnocent.propertiestoolkit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class DefaultSettings<T> {

  private Map<DefaultCondition, ReturnBehaviour> behaviourMap = new HashMap<>(1);

  public Setting when(DefaultCondition... conditions) {
    return new Setting(conditions);
  }

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

  public class Setting {

    private Set<DefaultCondition> conditions = new HashSet<>(1);
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

    public Setting thenDo(BiConsumer<String, String> task) {
      this.task = task;
      return this;
    }

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
