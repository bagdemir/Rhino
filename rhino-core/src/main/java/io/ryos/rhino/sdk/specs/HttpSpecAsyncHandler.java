package io.ryos.rhino.sdk.specs;

import io.netty.handler.codec.http.HttpHeaders;
import io.ryos.rhino.sdk.Simulation;
import io.ryos.rhino.sdk.reporting.MeasurementImpl;
import io.ryos.rhino.sdk.reporting.UserEvent;
import io.ryos.rhino.sdk.reporting.UserEvent.EventType;
import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseStatus;
import org.asynchttpclient.Response;
import org.asynchttpclient.netty.request.NettyRequest;

public class HttpSpecAsyncHandler implements AsyncHandler<Response> {

  private final String stepName;
  private final String specName;
  private final int userId;
  private final MeasurementImpl measurement;
  private final Simulation simulation;
  private volatile long start = -1;
  private volatile int status;
  private final Response.ResponseBuilder builder = new Response.ResponseBuilder();

  public HttpSpecAsyncHandler(int userId, String specName, String stepName, Simulation simulation) {
    this.measurement = new MeasurementImpl(specName, userId);
    this.specName = specName;
    this.userId = userId;
    this.simulation = simulation;
    this.stepName = stepName;
  }

  @Override
  public State onStatusReceived(final HttpResponseStatus responseStatus) {

    builder.reset();
    builder.accumulate(responseStatus);
    this.status = responseStatus.getStatusCode();

    return State.CONTINUE;
  }

  @Override
  public State onHeadersReceived(final HttpHeaders headers) {
    builder.accumulate(headers);
    return State.CONTINUE;
  }

  @Override
  public State onBodyPartReceived(final HttpResponseBodyPart bodyPart) {
    builder.accumulate(bodyPart);
    return State.CONTINUE;
  }

  @Override
  public void onThrowable(final Throwable t) {

  }

  @Override
  public Response onCompleted() {
    var elapsed = System.currentTimeMillis() - start;

    measurement.measure(stepName, String.valueOf(status));

    var userEventEnd = new UserEvent();
    userEventEnd.elapsed = elapsed;
    userEventEnd.start = start;
    userEventEnd.end = start + elapsed;
    userEventEnd.scenario = specName;
    userEventEnd.eventType = EventType.END;
    userEventEnd.id = userId;
    measurement.record(userEventEnd);

    simulation.dispatchEvents(measurement);

    return builder.build();
  }

  @Override
  public void onRequestSend(NettyRequest request) {

    this.start = System.currentTimeMillis();
    var userEventStart = new UserEvent();
    userEventStart.elapsed = 0;
    userEventStart.start = start;
    userEventStart.end = start;
    userEventStart.scenario = specName;
    userEventStart.eventType = EventType.START;
    userEventStart.id = userId;
    measurement.record(userEventStart);
  }
}
