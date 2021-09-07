/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.semantic.type;

import ma.vi.esql.syntax.EsqlPath;

import java.util.Map;

import static ma.vi.base.lang.Errors.checkArgument;

/**
 * The type for arrays.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class ArrayType extends AbstractType {
  public ArrayType(Type componentType) {
    super(arrayTypeName(componentType));
    this.componentType = componentType;
  }

  public ArrayType(ArrayType other) {
    super(other);
    this.componentType = other.componentType;
  }

  @Override
  public ArrayType copy() {
    return new ArrayType(this);
  }

  @Override
  public String translate(Target target, EsqlPath path, Map<String, Object> parameters) {
    return switch (target) {
      case SQLSERVER      -> "nvarchar(max)";
      case MARIADB, MYSQL -> "text";
      case HSQLDB         -> componentType.translate(target, path, parameters) + " array";
      default             -> componentType.translate(target, path, parameters) + "[]";
    };
  }

  public static String arrayTypeName(Type componentType) {
    checkArgument(componentType != null, "Component type for array cannot be null");
    return componentType.name() + "[]";
  }

  @Override
  public Kind kind() {
    return Kind.ARRAY;
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  /**
   * The type of the components of the array.
   */
  public final Type componentType;
}