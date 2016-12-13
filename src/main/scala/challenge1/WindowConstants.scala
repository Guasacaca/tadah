package challenge1

import java.text.DecimalFormat

object WindowConstants {

  //messages
  val usageMessage = "For successfully using this program, you should include a PATH to the input file (e.g. Usage: RollingWindow filename)"
  val fileIsNotThereMessage = " is not a valid path. Please introduce a valid file from your local system as input."
  val badFormatMessage = "The file doesn't have a well formed format. Please check it."

  //rolling window
  val windowLength = 60 //in seconds
  val lineFormat = """^([0-9]+)[ \t]([0-9]+\.[0-9]+)$""".r//regex for matching the lines
  val decimalFormatter = new DecimalFormat("#.#####")//5 decimals.

  //output rolling window
  val header = "%-12s".format("T")+"%-9s".format("V")+"%-4s".format("N")+"%-10s".format("RS")+
    "%-9s".format("MinV")+"%-9s".format("MaxV")+"\n"+
    "---------------------------------------------------"
}
