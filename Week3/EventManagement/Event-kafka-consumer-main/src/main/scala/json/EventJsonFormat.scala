package json

import spray.json._
import model.EventNotification

object EventJsonFormat extends DefaultJsonProtocol {
  implicit val formatEventNotification: RootJsonFormat[EventNotification] =
    jsonFormat10(EventNotification)  // adjust number of fields based on your model
}
