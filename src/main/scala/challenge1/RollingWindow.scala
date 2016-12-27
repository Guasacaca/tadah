package challenge1

import java.nio.file.{Files, Paths}

import scala.collection.immutable.Vector
import scala.io.Source
import scala.util.{Failure, Success, Try}
import WindowConstants._

object RollingWindow {

  case class PriceRatio(time:Long, ratio: Double)

  /**
   * Process one line of the file. Checks if match the format, and calculate everything for the active window. And prints it.
   * @param line line to process
   * @param exActives last round active price ratios
   * @return this round active price ratios. None if non.
   */
  def processLine(line:String, exActives:Option[Vector[PriceRatio]]): Option[Vector[PriceRatio]] = {
    val isALine = lineFormat.findAllIn(line)
      .matchData
      .next()
      //extracting the timestamp
      val nowTry = Try(isALine.group(1).toLong) match {
        case Success(time) => Right(time)
        case Failure(error) => Left(badFormatMessage)
      }
      //extracting the price ratio
      val priceRatioTry = Try(isALine.group(2).toDouble) match {
        case Success(price) => Right(price)
        case Failure(error) => Left(badFormatMessage)
      }

      //checking if timestamp and price ratio are in the correct format
      nowTry match {
        case Right(now) =>
          priceRatioTry match {
            case Right(priceRatio) =>
              val activeLines = exActives.getOrElse(Vector[PriceRatio]())
              val actives = (activeLines :+ PriceRatio(now, decimalFormatter.format(priceRatio).toDouble))
              val values = actives.foldLeft((0, 0.0, Double.MaxValue, 0.0)){ (acc,thisRatio) =>
                if (thisRatio.time >= now - windowLength) {
                  val min = Math.min(acc._3, thisRatio.ratio)
                  val max = Math.max(acc._4, thisRatio.ratio)
                  val sum = acc._2 + thisRatio.ratio
                  (acc._1+1,sum,min,max)
                } else {
                  acc
                }

              }
              println(s"$now  $priceRatio  ${"%-2s".format(values._1)} ${"%-8s".format(decimalFormatter.format(values._2))}  ${values._3}  ${values._4}")
              Some(actives)
            case Left(message) =>
              println(message)
              None
          }
        case Left(message)=>
          println(message)
          None
      }
  }

  /**
   * Process every line in the file, and call itself recursively (tail recursion!).
   * @param it iterator of lines (scala stream)
   * @param actives active prices ratio (in last window). None if non price ratio was active.
   * @return iterator and last active elements.
   */
  def processLines(it:Iterator[String], actives:Option[Vector[PriceRatio]]): (Iterator[String], Option[List[PriceRatio]]) = {
    if (it.hasNext) {
      val line = it.next()
      processLines(it, processLine(line, actives))
    } else {
      (it, None)
    }
  }

  /**
   * Process the file in path.
   * Scala streaming function gives a lazy iterator, therefore the
   * reading of the file can be done, even if it's too (avoiding memory issues)
   *
   * Expects a file with 2 columns. In the first one, a timestamp with a long (seconds).
   * The second one with the price ratio.
   *
   * It calculates the min, max, sum and quantity of price ratios per window (sliding window)
   *
   * The size of the window is set in the windowLength variable.
   *
   * @param path path of the file
   */
  def processFile(path:String): Unit = {
    require(Files.exists(Paths.get(path)), fileIsNotThereMessage)

    //first line
    println(header)

    val lines:Iterator[String] = Source.fromFile(path).getLines()

    processLines(lines, None)

  }

  def main (args: Array[String]) {
    //first argument is the path, I don't care about the other args
    val maybePath = Try(args.head) match {
      case Success(pathToFile:String) =>
        if (Files.exists(Paths.get(pathToFile))) Right(pathToFile)
        else Left(pathToFile+fileIsNotThereMessage)
      case Failure(ex) => Left(usageMessage)
    }
    //process file
    maybePath match {
      case Right(path) => processFile(path)
      case Left(errorMessage) => println(errorMessage)
    }
  }
}
