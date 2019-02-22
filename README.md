# PropertiesToolkit

A simple, lightweight utility to validate and parse values from the Java Properties file.

Currently, the `Properties` class does not provide a simple method of constraining user input. This makes basic property reading a bulky and tiresome task. This library provides a simple set of methods to minimise the amount of code required to retrieve properties, leading to:
- Cleaner, more readable code
- Minimal code repetition
- Faster writing
- Fewer bugs

## Examples
For these examples, we will assume that we have created and populated a `Properties` file, called `properties`. We will reference the following keys:

`maxUsers`: The maximum number of users that can simultaneously access our system.

`admin`: The name of the admin account for our system.

The following line is required to start reading the `properties` file:
```
PropertyReader reader = new PropertyReader(properties);
```

All examples assume this has been completed.


### Parsing
Values stored in a `Properties` file are saved as `String`s. These often need to be converted to another object type in order to be useful in our programs.

#### Primitive Wrappers
Conversion methods for all primitive wrapper classes are provided by default from the `PropertyReader` class. For example, to convert the property `"value"` to an `Integer`, we can do the following:
```
Integer value = reader.getInt("maxUsers").get();    // 50
```

#### Custom Conversions
Conversions can also be completed for custom objects. To do this, you'll need to define the function that will convert the `String` into the desired object type. The following example converts the property `"admin"` into a custom `User` object, using our constructor `User(String name, LocalDate initTime)`:
```
User adminUser =
  reader.getCustom("admin", name -> new User(name, LocalDate.now()))
        .get();
```

It may be possible that exceptions are thrown from the conversion process. In this case, it is possible, if desired, to handle these scenarios and return default values. See the Exceptions section.

### Imposing Constraints
Sometimes, we want to validate that the value we retrieve meet a set of criteria to be considered a valid value. This can be imposed using the `Property::addConstraint` method. You can define whatever checks you deem necessary from here. For example, if we want to enforce that the specified maximum number of users must be > 0, we can do the following:

```
Integer maxUsers = reader.getInt("maxUsers")
                         .addConstraint(users -> users > 0)
                         .get();
```

Mutliple constraints can also be added, as follows:
```
Integer maxUsers = reader.getInt("maxUsers")
                         .addConstraint(users -> users > 0)
                         .addConstraint(users -> users < 10_000)
                         .get();
```

If any of these constraints fail, we can either provide a default return value, or an exception will be thrown when calling the `Property::get` method.

### Default Values and Exceptions
There are three places where exceptions can be thrown while calling the `Property::get` method:
The property you're trying to parse is empty or missing:

| Case                                                      | Condition name                 | Exception                  |
| --------------------------------------------------------- |:------------------------------:|:--------------------------:|
| The property value is `null` or empty                     | `DefaultCondition.IS_EMPTY`    | `MissingPropertyException` |
| The value cannot be converted to the required object type | `DefaultCondition.PARSE_FAILS` | `InvalidTypeException`     |
| The value fails any of the constraints                    | `DefaultCondition.IS_INVALID`  | `ValidationException`      |

You can catch any of these specific exceptions or the parent exception, `PropertiesException`, while processing.

#### Default values
However, it's usually better to assign some default behaviour when these conditions are met. These can be specified using the `DefaultSettings` object. For example, let's try and get the maximum number of users from the properties file - if this property is not specified, cannot be parsed to an `Integer`, or fails validation, let's set this to a reasonable average of 50.

```
DefaultSettings<Integer> maxUsersDefault = new DefaultSettings<Integer>()
                                                 .when(DefaultConditions.values()) // Any DefaultCondition
                                                 .thenReturn(50);

Integer maxUsers = reader.getInt("maxUsers")
                         .withDefaultSettings(maxUsersDefault) // Add the default behaviour
                         .addConstraint(users -> users > 0)
                         .addConstraint(users -> users < 10_000)
                         .get();
```

We can also add a task to complete if these conditions are met. This could be useful if to, for example, log the warnings to a `Logger`:
```
DefaultSettings<Integer> maxUsersDefault = new DefaultSettings<Integer>()
                                                 .when(DefaultConditions.values()) // Any DefaultCondition
                                                   .thenDo((key, value) ->
                                                       LOGGER.warn("Key, " + key + ", is invalid: " + value))
                                                   .thenReturn(50);
```

Finally, we can express different behaviour and return values dependent on which condition is triggered:
```
DefaultSettings<Integer> maxUsersDefault = new DefaultSettings<Integer>()
                                                 .when(DefaultConditions.IS_EMPTY)
                                                   .thenDo((key, value) ->
                                                       LOGGER.info("Key, " + key + ", has not been specified"))
                                                   .thenReturn(50)
                                                 .when(DefaultCondition.IS_INVALID)
                                                   .thenDo((key, value) ->
                                                       LOGGER.info("Key, " + key + ", is invalid: " + value))
                                                   .thenReturn(20)
                                                 .when(DefaultCondition.PARSE_FAILS)
                                                   .thenDo((key, value) ->
                                                       LOGGER.info("Key, " + key + ", cannot be parsed to an int: " + value))
                                                   .thenReturn(10);
```

When a `DefaultCondition` is encountered, the `Property` will search to see if it has any default settings that cover this scenario. If no settings are found, or the settings do not specify how that condition should be handled, the appropriate exception is thrown.
