package avionics

import akka.actor.{Props, Actor, ActorLogging}

object Plane {
	case object GiveMeControl
}

class Plane extends Actor with ActorLogging {
	import Altimeter._
	import Plane._
	import EventSource._

	val altimeter = context.actorOf(Props[Altimeter], "Altimeter")
	val controls  = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")

    override def preStart() {
		altimeter ! RegisterListener(self)
	}
	
	def receive = {
		case AltitudeUpdate(altitude) => {
			log info(f"Altitude is now: $altitude%2.2f.")
		}
		case GiveMeControl => {
			log info("Plane giving control.")
			sender ! controls
		}
	}
}