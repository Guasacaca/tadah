import challenge2.{Echo, Tasks, Delay}
import challenge2.Tasks.Node
import org.junit.Test

class TasksTest {

  @Test
  def resetDelayTest(): Unit = {
    val d1 = new Delay("hello")
    d1.value = "hello"
    val d2 = new Delay("foo")
    d2.value = "foo"
    val d3 = new Delay("bar")
    d3.value = "bar"
    val d4 = new Delay("shh")
    d4.value = "shh"

    val tasks:Map[String, Node] = Map("hey" -> Node("hey", d1),
      "ho" -> Node("ho", d2),
      "lets" -> Node("lets", d3),
      "go" -> Node("go", d4),
      "thisIsNotdelay" -> Node("n4", new Echo("yo")))

    val allDelays1 = tasks.values
      .map(_.operation)
      .filter(_.isInstanceOf[Delay])
      .map(_.value)
    assert(allDelays1.map(!_.equals("tbb")).reduce(_&&_), "Some of the Delays values are tbb")

    Tasks.resetDelay(tasks)

    val allDelays2 = tasks.values
      .map(_.operation)
      .filter(_.isInstanceOf[Delay])
      .map(_.value)
    assert(allDelays2.map(_.equals("tbb")).reduce(_&&_), "Some of the Delays are not reset")
  }
}
