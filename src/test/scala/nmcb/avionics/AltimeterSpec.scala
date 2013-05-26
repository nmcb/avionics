package nmcb.avionics

import nmcb.avionics.Altimeter.{AltitudeUpdate, RateChange}

import org.scalatest.{WordSpecLike, BeforeAndAfterAll}
import org.scalatest.Matchers

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit, TestLatch, ImplicitSender}

import scala.concurrent.Await
import scala.concurrent.duration._

class AltimeterSpec extends TestKit(ActorSystem("AltimeterSpec"))
                            with ImplicitSender
                            with WordSpecLike
                            with Matchers
                            with BeforeAndAfterAll
{


   override def afterAll() { system.shutdown() }

   // We'll instantiate a Helper class for every test, making
   // things nicely reusable.
   class Helper
   {
      object EventSourceSpy
      {
         // The latch gives us fast feedback
         val latch = TestLatch(1)
      }

      // A special derivation of EventSource gives us hooks into concurrency
      trait EventSourceSpy extends EventSource
      {
         def sendEvent[T](event: T): Unit = EventSourceSpy.latch.countDown()

         def eventSourceReceive = Actor.emptyBehavior
      }

      // The slicedAltimeter constructs our Altimeter with the EventSourceSpy
      def slicedAltimeter = new Altimeter with EventSourceSpy

      def actor() = {
         val a = TestActorRef[Altimeter](Props(slicedAltimeter))
         (a, a.underlyingActor)
      }
   }

   "Altimeter" should {
      "record rate of climb changes" in new Helper
      {
         val (reference, real) = actor()
         real.receive(RateChange(1f))
         real.rateOfClimb should be(real.maxRateOfClimb)
      }
      "keep rate of climb changes within bounds" in new Helper
      {
         val (ref, obj) = actor()
         obj.receive(RateChange(2f))
         obj.rateOfClimb should be(obj.maxRateOfClimb)
      }
      "calculate altitude changes" in new Helper
      {
         val obj = system.actorOf(Props(Altimeter()))
         obj ! EventSource.RegisterListener(testActor)
         obj ! RateChange(1f)
         fishForMessage() {
            case AltitudeUpdate(altitude) if altitude == 0f =>
               false
            case AltitudeUpdate(altitude) =>
               true
         }
      }
      "send events" in new Helper
      {
         val (ref, _) = actor()
         Await.ready(EventSourceSpy.latch, 1.second)
         EventSourceSpy.latch.isOpen should be(true)
      }
   }
}