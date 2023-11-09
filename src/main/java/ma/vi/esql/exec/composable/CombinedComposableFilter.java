package ma.vi.esql.exec.composable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CombinedComposableFilter extends ComposableFilter {
  public CombinedComposableFilter(ComposableFilter.Op    op,
                                  boolean                exclude,
                                  List<ComposableFilter> filters) {
    super(exclude);
    this.op = op;
    this.filters = filters;
  }

  public CombinedComposableFilter(ComposableFilter.Op op,
                                  boolean             exclude,
                                  ComposableFilter... filters) {
    this(op, exclude, List.of(filters));
  }

  public CombinedComposableFilter(ComposableFilter.Op op,
                                  ComposableFilter... filters) {
    this(op, false, filters);
  }

  @Override
  public ComposableFilter replaceTable(Map<String, String> replacements) {
      return new CombinedComposableFilter(op,
                                          exclude(),
                                          filters.isEmpty()
                                        ? filters
                                        : filters.stream().map(c -> c.replaceTable(replacements)).toList());
  }

  public CombinedComposableFilter add(ComposableFilter filter) {
    List<ComposableFilter> f = new ArrayList<>(filters);
    f.add(filter);
    return new CombinedComposableFilter(op, exclude(), f);
  }

  public ComposableFilter.Op op() {
    return op;
  }

  public List<ComposableFilter> filters() {
    return filters;
  }

  private final ComposableFilter.Op op;
  private final List<ComposableFilter> filters;
}
