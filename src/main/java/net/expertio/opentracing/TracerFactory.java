package net.expertio.opentracing;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

import java.io.IOException;

public class TracerFactory {

  public static final void init(String serviceName, String serverUri) {
    if (serverUri == null) {
      serverUri = "http://localhost:9411/api/v1/spans";
    }

    Sender sender = OkHttpSender.create(serverUri);
    AsyncReporter reporter = AsyncReporter.builder(sender).build();

    Tracing tracer = Tracing.newBuilder().localServiceName(serviceName).reporter(reporter).build();

    Tracer trace = BraveTracer.create(tracer);
    GlobalTracer.register(trace);

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread() {
              @Override
              public void run() {
                System.err.println("*** shutting down tracer");
                reporter.close();
                try {
                  sender.close();
                } catch (IOException ex) {
                  ex.printStackTrace();
                }
              }
            });
  }
}
