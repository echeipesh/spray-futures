package tutorial

import akka.actor._
import spray.util.LoggingContext
import spray.routing.ExceptionHandler
import spray.http.StatusCodes._

class ServiceActor extends Service with Actor {
  // the HttpService trait (which GeoTrellisService will extend) defines
  // only one abstract member, which connects the services environment
  // to the enclosing actor or test.
  def actorRefFactory = context

  def receive = runRoute(rootRoute)
}