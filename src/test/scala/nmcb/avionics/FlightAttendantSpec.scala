package nmcb.avionics

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestKit, TestActorRef, ImplicitSender}
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import com.typesafe.config.ConfigFactory

object TestFlightAttendant
{
   def apply() = new FlightAttendant
      with AttendantResponsiveness
   {
      val maxResponseTimeMS = 1
   }
}

class FlightAttendantSpec
   extends TestKit(ActorSystem("FlightAttendantSpec", ConfigFactory.parseString("akka.scheduler.tick-duration = 1ms")))
   with ImplicitSender
   with WordSpecLike
   with Matchers
{

   import FlightAttendant._

   "FlightAttendant" should {
      "get a drink when asked" in {
         for (i <- 0 to 100) {
            val a = TestActorRef(Props(TestFlightAttendant()))
            a ! GetDrink("Soda")
            expectMsg(Drink("Soda"))
         }
      }
   }
}


