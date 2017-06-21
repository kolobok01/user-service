package net.expertio.opentracing;

import io.opentracing.SpanContext;
import io.opentracing.contrib.OpenTracingContextKey;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.util.GlobalTracer;

import javax.ws.rs.core.MultivaluedMap;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;

public class OpenTracingJersey {

  private OpenTracingJersey() {}

  public static void injectSpanHeaders(final MultivaluedMap<String, ? super Object> httpHeaders) {
    TextMap traceCarrier = new TraceCarrier(httpHeaders);
    GlobalTracer.get()
        .inject(
            OpenTracingContextKey.activeSpan().context(),
            Format.Builtin.HTTP_HEADERS,
            traceCarrier);
  }

  public static SpanContext extractSpanHeaders(
      final MultivaluedMap<String, ? extends Object> httpHeaders) {
    TextMap traceCarrier = new TraceCarrier(httpHeaders);
    return GlobalTracer.get().extract(Format.Builtin.HTTP_HEADERS, traceCarrier);
  }

  private static class TraceCarrier<T extends Object> implements TextMap {

    final MultivaluedMap<String, T> httpHeaders;

    public TraceCarrier(MultivaluedMap<String, T> httpHeaders) {
      this.httpHeaders = httpHeaders;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
      return new Iterator<Map.Entry<String, String>>() {
        Iterator<String> internal = httpHeaders.keySet().iterator();

        @Override
        public boolean hasNext() {
          return internal.hasNext();
        }

        @Override
        public Map.Entry<String, String> next() {
          String key = internal.next();
          return new AbstractMap.SimpleImmutableEntry(key, httpHeaders.getFirst(key));
        }
      };
    }

    @Override
    public void put(String key, String value) {
      httpHeaders.putSingle(key, (T) value);
    }
  }
}
