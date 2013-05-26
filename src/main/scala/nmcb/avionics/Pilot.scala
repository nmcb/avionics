package nmcb.avionics

import akka.actor.{ActorRef, Actor}
import nmcb.avionics.Plane.Controls

object Pilots
{
   case object ReadyToGo
   case object RelinquishControl
}

class Pilot extends Actor
{

   import Pilots._
   import Plane._

   var controls : ActorRef = context.system.deadLetters
   var copilot  : ActorRef = context.system.deadLetters
   var autopilot: ActorRef = context.system.deadLetters

   val copilotName = context.system.settings.config
      .getString("nmcb.avionics.flightcrew.copilotName")

   def receive = {
      case ReadyToGo => {
         context.parent ! GiveMeControl
         copilot = context.actorFor("../" + copilotName)
         autopilot = context.actorFor("../Autopilot")
      }
      case Controls(controlSurfaces) => {
         controls = controlSurfaces
      }
   }
}

class Copilot extends Actor
{

   import Pilots._

   var controls: ActorRef = context.system.deadLetters
   var pilot   : ActorRef = context.system.deadLetters
   var autopilot          = context.system.deadLetters

   val pilotName = context.system.settings.config
      .getString("nmcb.avionics.flightcrew.pilotName")

   def receive = {
      case ReadyToGo => {
         pilot = context.actorFor("../" + pilotName)
         autopilot = context.actorFor("../Autopilot")
      }
      case Controls(controlSurfaces) => {
         controls = controlSurfaces
      }
   }
}

class Autopilot extends Actor
{
   var controls: ActorRef = context.system.deadLetters

   def receive = {
      case Controls(controlSurfaces) => {
         controls = controlSurfaces
      }
   }
}
