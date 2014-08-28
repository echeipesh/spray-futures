package tutorial

import akka.actor._
import akka.io.IO
import spray.can.Http

object Main {
  def main(args: Array[String]) {
    // we need an ActorSystem to host our service
    implicit val system = ActorSystem()

    //create our service actor
    val service = system.actorOf(Props[ServiceActor], "geotrellis-service")

    //bind our actor to HTTP
    IO(Http) ! Http.Bind(service, interface = "localhost", port = 8000)
  }
}
