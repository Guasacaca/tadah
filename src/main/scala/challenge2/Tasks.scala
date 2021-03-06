package challenge2

import java.nio.file.{Paths, Files}

import TasksConstants._
import scala.io.Source
import challenge2.Network._
import scala.util.{Failure, Success, Try}

object Tasks {

  case class Node(name:String, operation:Operation)

  /**
   * Process a line. Defines if it's a task, link or process and create structures
   * @param it iterator over lines
   * @param tasks tasks that have been read
   * @param links links that have been read (only if the tasks were created before)
   * @param root root task
   * @return tasks and links updates
   */
  def processLine(it:Iterator[String], tasks:Map[String, Node], links:Map[Node, List[Node]], root:Option[Node]): (Map[String, Node], Map[Node, List[Node]]) = {
    if (it.hasNext){
      //this iteration line
      val line = it.next()

      // regex iterators
      val taskIt = taskStatementFormat
        .findAllIn(line)
        .matchData
      val linkIt = linkStatementFormat
        .findAllIn(line)
        .matchData
      val processIt = processStatementFormat.findAllIn(line)
        .matchData

      //it's a task
      if (taskIt.hasNext) {
        val isATask = taskIt.next()
        val name = isATask.group(1)
        val op = isATask.group(2) match {
          case "echo" => new Echo(name)
          case "reverse" => new Reverse(name)
          case "delay" => new Delay(name)
          case "noop" => new Noop(name)
        }
        val newNode = Node(name, op)

        //only first appearance of name counts.
        val node = tasks.get(name) match {
          case Some(n) => n //ignore newnode.
          case None => newNode
        }
        //first task -> first Node! (root)
        val first:Node = root.getOrElse(node)
        val newTasks:Map[String, Node] = tasks + (name -> node)
        processLine(it, newTasks, links, Some(first) )

      } //it's a link
      else if (linkIt.hasNext) {
        val isALink = linkIt.next()
        val name1 = isALink.group(1)
        val name2 = isALink.group(2)
        val theNode1 = tasks.get(name1)
        val theNode2 = tasks.get(name2)
        //both should already exist
        if (theNode1.isDefined && theNode2.isDefined) {

          val nodesList:List[Node] = links.getOrElse(theNode1.get, List[Node]()) :+ theNode2.get
          val newLinks:Map[Node, List[Node]] = links + (theNode1.get -> nodesList)
          processLine(it, tasks, newLinks, root)
        } else {
          //else, ignore link
          processLine(it, tasks, links, root)
        }
        //it's a process
      } else if (processIt.hasNext){
        val isAProcess = processIt.next()
        val words = isAProcess.group(1).split(" ").toList
        if (root.isDefined){
          val numberDelays = tasks.values.count(_.operation.isInstanceOf[Delay])
          executePipeline(root.get, words++ List.fill(numberDelays)(""), links)
          //reset delays!
          resetDelay(tasks)
          println()

        }
        //else ignore process.
        processLine(it, tasks, links, root)
      } else {
        //ignore that line if doesn't match anything
        processLine(it, tasks, links, root)
      }
    } else {
      (tasks,links)
    }
  }

  /**
   * Reset the delays to initial tbb values
   * @param tasks tasks with delays operation reset
   */
  def resetDelay(tasks:Map[String, Node]):Unit = {
    tasks.map(x => x._2)
    .filter(_.operation.isInstanceOf[Delay])
    .foreach(_.operation.asInstanceOf[Delay].reset())
  }

  /**
   * Process the file
   * @param path path to file
   */
  def processFile(path:String): Unit ={
    require(Files.exists(Paths.get(path)), fileIsNotThereMessage)
    val lines = Source.fromFile(path).getLines()
    processLine(lines, Map[String, Node](), Map[Node, List[Node]](), None )
  }

  def main (args: Array[String]) {
    //first argument is the path, I don't care about the other args
    val maybePath = Try(args.head) match {
      case Success(pathToFile:String) =>
        if (Files.exists(Paths.get(pathToFile))) Right(pathToFile) else Left(pathToFile+fileIsNotThereMessage)
      case Failure(ex) => Left(usageMessage)
    }
    //process file
    maybePath match {
      case Right(path) => processFile(path)
      case Left(errorMessage) => println(errorMessage)
    }
  }
}
