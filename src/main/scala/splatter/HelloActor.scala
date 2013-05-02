package splatter

import akka.actor.{Actor,Props,ActorSystem}

class HelloActor extends Actor
{
	def receive = {
		case "Good Morning" => println("Him: Hi There!")
		case "Wanker"       => println("Him: Fuck off!")
	}
}

object TalkingMain
{
	val system = ActorSystem("HelloActor")
	val actor  = system.actorOf(Props[HelloActor], "Shake")
	
	def send(msg: String) {
		println(s"Me:  $msg.")
		actor ! msg
	}
	
	def main(args: Array[String]) {
		send("Good Morning")
		send("Wanker")
		system.shutdown()
	}
}