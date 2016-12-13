package challenge2

object TasksConstants {

  //messages
  val usageMessage = "For successfully using this program, you should include a PATH to the input file (e.g. Usage: Tasks filename)"
  val fileIsNotThereMessage = " is not a valid path. Please introduce a valid file from your local system as input."
  val badFormatMessage = "The file doesn't have a well formed format. Please check it."

  //tasks
  val taskStatementFormat = """^task[ \t](\w+)[ \t](echo|delay|noop|reverse)$""".r
  val linkStatementFormat = """^link[ \t](\w+)[ \t](\w+)$""".r
  val processStatementFormat = """^process[ \t]([ \t\w]+)$""".r

  //operation
  val echo = "echo"
  val reverse = "reverse"
  val delay = "delay"
  val noop = "noop"

  //messages for tasks
  val taskIsNotThereMessage = "You tried to create a link, with unexistent tasks"
}
