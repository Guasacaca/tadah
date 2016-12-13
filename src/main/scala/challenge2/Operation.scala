package challenge2

sealed trait Operation {

  def name:String
  var value:String

  def execute(input:String):String
}

class Echo(echoName:String) extends Operation {
  override def name: String = echoName

  override def execute(input: String): String = input+input

  override var value: String = _
}

class Reverse(reverseName:String) extends Operation {
  override def name: String = reverseName

  override def execute(input: String): String = input.reverse

  override var value: String = _
}

class Noop(noopName:String) extends Operation {
  override def name: String = noopName

  override def execute(input: String): String = input

  override var value: String = _
}

class Delay(delayName: String) extends Operation {
  override def name: String = delayName

  override def execute(input: String): String = {
    val exValue = value
    this.value = input
    exValue
  }
  override var value: String = "tbb"

  def reset(): Unit = {
    this.value = "tbb"
  }
}
