package nmcb.avionics

import akka.actor.{ActorRef, Props, Actor, ActorLogging}

object Plane
{
   case object GiveMeControl
   case class Controls(controls: ActorRef)
}

class Plane extends Actor with ActorLogging
{

   import Altimeter._
   import Plane._
   import EventSource._

   val cfgstr = "nmcb.avionics.flightcrew"
   val config = context.system.settings.config

   val altimeter = context.actorOf(Props(Altimeter()), "Altimeter")
   val controls  = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")

   val pilot           = context.actorOf(Props[Pilot], config.getString(s"$cfgstr.pilotName"))
   val copilot         = context.actorOf(Props[Copilot], config.getString(s"$cfgstr.copilotName"))
   val autopilot       = context.actorOf(Props[Autopilot], "Autopilot")
   val flightAttendant = context.actorOf(Props(LeadFlightAttendant()), config.getString(s"$cfgstr.leadAttendantName"))


   override def preStart() {
      altimeter ! RegisterListener(self)
      List(pilot, copilot) foreach { _ ! Pilots.ReadyToGo }
   }

   def receive = {
      case AltitudeUpdate(altitude) => {
         log info (f"Altitude is now: $altitude%2.2f.")
      }
      case GiveMeControl => {
         log info ("Plane giving control.")
         sender ! Controls(controls)
      }
   }
}

object PlanePathChecker
{
   def main(args: Array[String]) {
      val system = akka.actor.ActorSystem("PlaneSimulation")
      val lead = system.actorOf(Props[Plane], "Plane")

      Thread.sleep(2000)
      system.shutdown()
   }
}