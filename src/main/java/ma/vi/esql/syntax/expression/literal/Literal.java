/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.expression.literal;

import ma.vi.base.tuple.T2;
import ma.vi.esql.semantic.type.*;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.expression.Expression;
import ma.vi.esql.translation.TranslationException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static ma.vi.esql.syntax.expression.literal.StringLiteral.escapeEsqlString;

/**
 * Parent of literals in ESQL.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public abstract class Literal<V> extends Expression<V, String> {
  public Literal(Context context, V value) {
    super(context, value);
  }

  @SafeVarargs
  public Literal(Context context,
                 V value,
                 T2<String, ? extends Esql<?, ?>>... children) {
    super(context, value, children);
  }

  public Literal(Context context,
                 V value,
                 List<? extends Esql<?, ?>> children) {
    super(context, value, children);
  }

  public Literal(Literal<V> other) {
    super(other);
  }

  @SafeVarargs
  public Literal(Literal<V> other, V value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public abstract Literal<V> copy();

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public abstract Literal<V> copy(V value, T2<String, ? extends Esql<?, ?>>... children);

  public static Literal<?> makeLiteral(Context context, String value, Type type) {
    if (value == null) {
      return new NullLiteral(context);

    } else if (type == Types.StringType
        || type == Types.TextType) {
      return new StringLiteral(context, '\'' + escapeEsqlString(value) + '\'');

    } else if (type == Types.UuidType) {
      return new UuidLiteral(context, UUID.fromString(value));

    } else if (type == Types.BoolType) {
      return new BooleanLiteral(context, Boolean.valueOf(value));

    } else if (type == Types.FloatType || type == Types.DoubleType) {
      return new FloatingPointLiteral(context, value);

    } else if (type == Types.ByteType
            || type == Types.ShortType
            || type == Types.IntType
            || type == Types.LongType) {
      return new IntegerLiteral(context, Long.valueOf(value));

    } else if (type == Types.TimeType) {
      return new DateLiteral(context, value);

    } else if (type == Types.DatetimeType) {
      return new DateLiteral(context, value);

    } else if (type == Types.DateType) {
      return new DateLiteral(context, value);

    } else if (type instanceof ArrayType arrayType) {
      int pos = value.indexOf('[');
      String elements = value.substring(pos + 1, value.length() - 1);
      List<BaseLiteral<?>> array = new ArrayList<>();
      for (String element: elements.split(",")) {
        array.add((BaseLiteral<?>)makeLiteral(context, element, arrayType.componentType));
      }
      return new BaseArrayLiteral(context, arrayType.componentType, array);
    }
    throw new TranslationException("Cannot make a literal of " + value + " (" + value.getClass() + ')');
  }

  /**
   * Creates a literal representation of the value based on its type.
   */
  public static Literal<?> makeLiteral(Context context, Object value) {
    if (value == null) {
      return new NullLiteral(context);

    } else if (value == JSONObject.NULL) {
      return new NullLiteral(context);

    } else if (value instanceof String) {
      return new StringLiteral(context, '\'' + escapeEsqlString(value.toString()) + '\'');

    } else if (value instanceof UUID) {
      return new UuidLiteral(context, (UUID)value);

    } else if (value instanceof Boolean) {
      return new BooleanLiteral(context, (Boolean)value);

    } else if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
      return new FloatingPointLiteral(context, String.valueOf(value));

    } else if (value instanceof Byte || value instanceof Short || value instanceof Integer ||
        value instanceof Long || value instanceof BigInteger) {
      return new IntegerLiteral(context, ((Number)value).longValue());

    } else if (value instanceof Time) {
      return new DateLiteral(context, DateLiteral.TimeFormat.format((Time)value));

    } else if (value instanceof Timestamp) {
      return new DateLiteral(context, DateLiteral.DateTimeFormat.format((Timestamp)value));

    } else if (value instanceof java.sql.Date) {
      return new DateLiteral(context, DateLiteral.DateFormat.format((java.sql.Date)value));

    } else if (value instanceof Date) {
      return new DateLiteral(context, DateLiteral.DateTimeFormat.format((Date)value));

    } else if (value instanceof Interval) {
      return new IntervalLiteral(context, value.toString());

    } else if (value instanceof JSONArray a) {
      Type type = null;
      if (a.length() > 0) {
        Class<?> componentType = null;
        for (int i = 0; i < a.length(); i++) {
          Object element = a.get(i);
          if (element != null) {
            componentType = element.getClass();
            break;
          }
        }
        if (componentType != null) {
          type = Types.typeOf(componentType);
          if (type.kind() != Kind.BASE) {
            throw new TranslationException("Only arrays of base types are supported. " + type + " is not a base type.");
          }
        }
      }
      if (type == null) {
        /*
         * Assume string type for empty arrays or for those containing only
         * null entries, whereby the component-type cannot be dynamically
         * determined.
         */
        type = Types.StringType;
      }
      List<BaseLiteral<?>> array = new ArrayList<>();
      for (int i = 0; i < a.length(); i++) {
        array.add((BaseLiteral<?>)makeLiteral(context, a.get(i)));
      }
      return new BaseArrayLiteral(context, type, array);

    } else if (value.getClass().isArray()) {
      Class<?> componentType = value.getClass().getComponentType();
      Type type = Types.typeOf(componentType);
      if (type.kind() != Kind.BASE) {
        throw new TranslationException("Only arrays of base types are supported. " + type + " is not a base type.");
      }
      List<BaseLiteral<?>> array = new ArrayList<>();
      for (int i = 0; i < Array.getLength(value); i++) {
        array.add((BaseLiteral<?>)makeLiteral(context, Array.get(value, i)));
      }
      return new BaseArrayLiteral(context, type, array);
    }
    throw new TranslationException("Cannot make a literal of " + value + " (" + value.getClass() + ')');
  }
}