package tutorial

import java.util.concurrent.Executors

import spray.routing.{Route, ExceptionHandler, HttpService}
import spray.http.MediaTypes
import spray.http.StatusCodes.InternalServerError
import spray.util.LoggingContext
import scala.concurrent._
import scala.concurrent.forkjoin.ForkJoinPool
import scala.util.{Failure, Success}

trait Service extends HttpService  {
  implicit val ec = ExecutionContext.fromExecutorService(new ForkJoinPool(123))

  def rootRoute = pingRoute

  def block(delay: Long): String = {
    println(s"Blocking for $delay secs...")
    blocking {
      Thread.sleep(delay * 1000) //simulates long-running database query, HTTP request, filesystem access, etc
    }
    println("Done blocking")
    "Done blocking"
  }

  def poolFuture[T](body: => T): Future[T] = future(body)(ec)

  /**
   * http://localhost:8000/ping
   */
  def pingRoute = path("ping") {
    get {
      complete(poolFuture(block(5000)))
    }
  }

  /**
   * This will be picked up by the runRoute(_) and used to intercept Exceptions
   */
  implicit def TutorialExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      case e: Exception =>
        requestUri { uri =>
          complete(InternalServerError, s"Message: ${e.getMessage}\n Trace: ${e.getStackTrace.mkString("</br>")}" )
        }
    }
}