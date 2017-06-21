package net.expertio.opentracing;

import io.grpc.Context;
import io.opentracing.Span;

/**
 * A {@link io.grpc.Context} key for the parent OpenTracing trace state.
 *
 * <p>Can be used to get the parent span, or to set the parent span for a scoped unit of work.
 */
public class OpenTracingParentContextKey {

  public static final String KEY_NAME = "io.opentracing.parent-span";
  private static final Context.Key<Span> key = Context.key(KEY_NAME);

  /** @return the parent span for the current request */
  public static Span parentSpan() {
    return key.get();
  }

  /** @return the OpenTracing context key */
  public static Context.Key<Span> getKey() {
    return key;
  }
}
