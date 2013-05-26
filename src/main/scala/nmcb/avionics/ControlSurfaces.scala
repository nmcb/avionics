package nmcb.avionics

import akka.actor.{Actor, ActorRef}

class ControlSurfaces(altimeter: ActorRef) extends Actor
{

   import ControlSurfaces._
   import Altimeter._

   def receive = {
      case StickBack(amount) => {
         altimeter ! RateChange(amount)
      }
      case StickForward(amount) => {
         altimeter ! RateChange(-1 * amount)
      }
   }
}

object ControlSurfaces
{
   case class StickBack(amount: Float)
   case class StickForward(amount: Float)
}

