# Properties Toolkit

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

### Annotation-driven parsing
Annotations can be directly to instance fields. Here's a simple example:

```java
import java.util.Properties;
import org.richardinnocent.propertiestoolkit.annotations.FromProperty;
import org.richardinnocent.propertiestoolkit.annotations.PropertiesBean;
import org.richardinnocent.propertiestoolkit.annotations.constraints.NumberMustBePositive;

public class MyAppProperties extends PropertiesBean {

  @FromProperty(key = "admin")
  private String adminAccount;
 
  @FromProperty(constraints = NumberMustBePositive.class)
  private int maxUsers;

  public MyAppProperties(Properties properties) {
    super(properties);
  }

  public static void main(String[] args){
    Properties properties = new Properties();
    properties.put("admin", "myAdmin");
    properties.put("maxUsers", "50");
    
    MyAppProperties appProperties = new MyAppProperties(properties);
    System.out.println(appProperties.adminAccount); // myAdmin
    System.out.println(appProperties.maxUsers);     // 50
  }
  
}
```

Let's step through a few of the important lines from this example.

```java
public class MyAppProperties extends PropertiesBean
```
Each annotation-driven bean needs to extend the `PropertiesBean` class. This will ensure that all appropriate fields are initialised when a new instance is created.

```java
@FromProperty(key = "admin")
private String adminAccount;
```
The `FromProperty` annotation is used to specify that the `adminAccount` field is to be set based on the properties file. We have stated the key that should correspond to this value is `admin`.

```java
@FromProperty(constraints = NumberMustBePositive.class)
private int maxUsers;
```
Again, the `FromProperty` annotation is used on this field. In this case, however, there are a few differences:
- No key is specified. In this scenario, the name of the field is used.
- A constraint (`NumberMustBePositive`) is specified. After extracting the property from the `Properties` instance and mapping it to an `int`, this constraint gives confidence that `maxUsers > 0`. If the properties file declared `maxUsers=-12`, an exception would be thrown. There are different ways that these exceptions can be handled, which will be reviewed shortly.

#### Handling custom object types
The `FromProperty` annotation has an additional field that can be set, `extractor`. By default, a `GenericExtractor` is used. This can handle the build of any primitive or corresponding wrapper. If the field that's being set is none of these types, the field is attempted to be instantiated by using a constructor for that object type that takes a single string.

However, this isn't always appropriate. For example, a custom date format could be used to represent date/time objects. In this case, a custom extractor can be specified, such as the following:
```java
import org.richardinnocent.propertiestoolkit.annotations.PropertyExtractor;

public class MyObjectExtractor implements PropertyExtractor<MyObject> {

  @Override
  public Function<String, MyObject> getExtractionMethod() {
    return text -> new MyObject(text, 12);
  }

}
```
This extractor class can now be applied to a field.
```java
@FromProperty(extractor = MyObjectExtractor.class)
private MyObject myObject;
```

### Imposing custom constraints
This library comes equipped with a very small set of out-of-the-box constraints that can be used straight away. However, a lot of the time, these aren't going to be sufficient for our purposes. What if, as well as imposing that `maxUsers > 0`, we want to be sure that `maxUsers < 10_000`. Creating custom constraints is easy:
```java
import org.richardinnocent.propertiestoolkit.annotations.constraints.PropertyConstraint;

public class IntegerLessThanTenThousand implements PropertyConstraint {

  public IntegerLessThanTenThousand() {
    super(Integer.class);
  }

  @Override
  public Predicate<Integer> getConstraint() {
    return i -> i < 10_000;
  }

}
```

We can apply this to our original field, alongside the previous constraint.
```java
@FromProperty(constraints = {NumberMustBePositive.class, IntegerLessThanTenThousand.class})
private int maxUsers;
```
Any number of constraints can be added to a field.

### Handling errors
There are a few error types we can encounter when processing.

| Case                                                                                                         | Condition name                 | Exception                  |
| ------------------------------------------------------------------------------------------------------------ |:------------------------------:|:--------------------------:|
| The property value is `null` or empty                                                                        | `DefaultCondition.IS_EMPTY`    | `MissingPropertyException` |
| The value cannot be converted to the required object type (e.g. a `RuntimeException` is thrown when mapping) | `DefaultCondition.PARSE_FAILS` | `InvalidTypeException`     |
| The value fails any of the constraints                                                                       | `DefaultCondition.IS_INVALID`  | `ValidationException`      |

The way that these should be handled are defined within the chosen `PropertyExtractor`. The default behaviour is to throw the exceptions, but we can override this if we want to.

```java
import org.richardinnocent.propertiestoolkit.annotations.PropertyExtractor;

public class MyObjectExtractor implements PropertyExtractor<MyObject> {

  @Override
  public Function<String, MyObject> getExtractionMethod() {
    return text -> new MyObject(text, 12);
  }

  @Override
  public DefaultSettings<MyObject> getDefaultSettings() {
    return new DefaultSettings<MyObject>()
                 .when(DefaultConditions.values()) // Any DefaultCondition
                   .thenDo((key, value) ->
                              System.out.println("Key, " + key + ", is set to a bad value: " + value))
                   .thenReturn(new MyObject("", 0));
  }

}
```
In this example, if any errors are thrown while trying to parse any values extracted using `MyObjectExtractor`, an appropriate message is printed to the console, and default object is returned instead.

We can implement different behaviour based on the type that is thrown, if we want to.
```java
return new DefaultSettings<MyObject>()
             .when(DefaultConditions.IS_EMPTY)
               .thenReturn(new MyObject("", 0))
             .when(DefaultConditions.PARSE_FAILS)
               .thenDo((key, value) ->
                          System.out.println("Key, " + key + " could not be parsed from value: " + value))
               .thenReturn(new MyObject("", 0));
```
With these defaults conditions, the following will occur:

| Case                                                      | Result                                                                                |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| The property value is `null` or empty                     | An appropriate default instance is returned.                                          |
| The value cannot be converted to the required object type | A message is printed to the console, and an appropriate default instance is returned. |
| The value fails any of the constraints                    | An `ValidationException` is thrown at runtime.                                        |


## Parsing without annotations
It's possible to achieve the same behaviour without using annotations at all. An example is provided below for this.

```java
import org.richardinnocent.propertiestoolkit.PropertyReader;
import org.richardinnocent.propertiestoolkit.DefaultSettings;

public class NoAnnotationPropertyReader {

  public void runExample(Properties properties) {
    PropertyReader reader = new PropertyReader(properties);
    
    String adminAccount = reader.getString("admin").get();

    int maxUsers = reader.getInt("maxUsers")
                         .addConstraint(i -> i > 0)
                         .addConstraint(i -> i < 10_000)
                         .get();

    DefaultSettings<MyObject> myObjectDefaultSettings =
        new DefaultSettings<MyObject>()
                     .when(DefaultConditions.IS_EMPTY)
                       .thenReturn(new MyObject("", 0))
                     .when(DefaultConditions.PARSE_FAILS)
                       .thenDo((key, value) ->
                                  System.out.println("Key, " + key + " could not be parsed from value: " + value))
                       .thenReturn(new MyObject("", 0));
    
    MyObject myObject = reader.getCustom("myObject", text -> new MyObject(text, 12))
                              .withDefaultSettings(myObjectDefaultSettings)
                              .get();
  } 

}
```

For further information on extracting values without using annotations, consult the Javadoc.
