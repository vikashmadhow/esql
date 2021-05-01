/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A simple type equivalent to a primitive type. BaseTypes are used
 * to construct the more complex arrays, composites and relations types.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class BaseType extends AbstractType {
  BaseType(String name, int size, boolean integral,
           Map<Target, String> translations) {
    super(name);
    this.size = size;
    this.integral = integral;
    this.translations = translations;
  }

  public BaseType(BaseType other) {
    super(other);
    this.size = other.size;
    this.integral = other.integral;
    this.translations = new HashMap<>(other.translations);

  }

  @Override
  public BaseType copy() {
    /*
     * Base types are immutable, no need for copy.
     */
    return this;
  }

  @Override
  public String translate(Target target, Map<String, Object> parameters) {
    return target == Target.ESQL             ? name() :
            translations.containsKey(target) ? translations.get(target) : translations.get(Target.ALL);
  }

  @Override
  public Kind kind() {
    return Kind.BASE;
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  /**
   * The size in bytes of a value of this type. Used to compute type
   * promotion where a larger-sized type dominates a smaller-sized one.
   */
  public final int size;

  /**
   * Whether this is an integral numeric type or not.
   */
  public final boolean integral;

  /**
   * Names of base types follow this pattern of a simple identifier.
   */
  public static final Pattern BASE_TYPE = Pattern.compile("([A-Za-z_][A-Za-z0-9_])*");

  /**
   * Translation from the source type to different targets.
   */
  private final Map<Target, String> translations;
}