/*
 * Copyright (c) 2020 Vikash Madhow
 */

package ma.vi.esql.syntax.define.struct;

import ma.vi.base.tuple.T2;
import ma.vi.esql.database.Database;
import ma.vi.esql.database.EsqlConnection;
import ma.vi.esql.database.Structure;
import ma.vi.esql.exec.Result;
import ma.vi.esql.exec.env.Environment;
import ma.vi.esql.semantic.type.Struct;
import ma.vi.esql.syntax.Context;
import ma.vi.esql.syntax.Esql;
import ma.vi.esql.syntax.EsqlPath;
import ma.vi.esql.syntax.define.Define;
import org.pcollections.PMap;

import static ma.vi.esql.semantic.type.Type.dbTableName;
import static ma.vi.esql.translation.Translatable.Target.ESQL;

/**
 * Drop struct statement.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class DropStruct extends Define {
  public DropStruct(Context context, String name) {
    super(context, "DropStruct", T2.of("name", new Esql<>(context, name)));
  }

  public DropStruct(DropStruct other) {
    super(other);
  }

  @SafeVarargs
  public DropStruct(DropStruct other, String value, T2<String, ? extends Esql<?, ?>>... children) {
    super(other, value, children);
  }

  @Override
  public DropStruct copy() {
    return new DropStruct(this);
  }

  /**
   * Returns a shallow copy of this object replacing the value in the copy with
   * the provided value and replacing the specified children in the children list
   * of the copy.
   */
  @Override
  public DropStruct copy(String value, T2<String, ? extends Esql<?, ?>>... children) {
    return new DropStruct(this, value, children);
  }

  @Override
  protected String trans(Target               target,
                         EsqlConnection       esqlCon,
                         EsqlPath             path,
                         PMap<String, Object> parameters,
                         Environment          env) {
    return "drop type " + (target == ESQL ? name() : dbTableName(name(), target));
  }

  @Override
  protected Object postTransformExec(Target               target,
                                     EsqlConnection       esqlCon,
                                     EsqlPath             path,
                                     PMap<String, Object> parameters,
                                     Environment          env) {
    Database db = esqlCon.database();
    Structure structure = db.structure();
    if (structure.structExists(name())) {
      Struct struct = structure.struct(name());
      structure.database.dropTable(esqlCon, struct.id());
      structure.remove(struct);
    }
    return Result.Nothing;
  }

  public String name() {
    return childValue("name");
  }
}
