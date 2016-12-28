package challenge2

import challenge2.TasksReloaded.Node

object NetworkReloaded {
  /**
   * An active node is a node that will be executed in this round (time)
   * @param input input event
   * @param node node with action to take
   */
  case class ActiveNode(input:String, node:Node, time:Int)

  /**
   * Execute the pipeline for a process
   * @param first root task.
   * @param words words to execute
   * @param links links of the graph (network)
   * @return
   */
  def executePipeline(first:Node, words:List[String], links:Map[Node, Vector[(Node, Int)]]): Unit = {
    val initials = words.zipWithIndex
      .map(x => ActiveNode(x._1, first, x._2))
      .toVector
    runActiveNodes(initials, links, 0)
  }

  /**
   * run this level (time) of active nodes
   *
   * @param active active nodes at this level or time.
   * @param links links of the network
   * @return new list of active nodes (next time)
   */
  def runActiveNodes(active: Vector[ActiveNode], links:Map[Node, Vector[(Node, Int)]], currentTime:Int): Option[Vector[ActiveNode]] = {
    //check if any active node is repeated, if so -> concat.
    val (concatenateRepeated, notForThisTime) = active
      .partition(_.time==currentTime)
    val concatenated = concatenateRepeated
      .groupBy(_.node)
      .map(x => (x._1, x._2.map(n => n.input).reduce(_.concat(_))) )
      .map(x => ActiveNode(x._2, x._1, currentTime))
    val a = concatenated.flatMap{ thisActiveNode =>
      val output = thisActiveNode.node
        .operation
        .execute(thisActiveNode.input)
      val nexts = nextNode(thisActiveNode.node, links)
      if (nexts.isDefined){
        nexts.get
          .map(n => (output, n._1, currentTime+1, n._2))
      } else {//last node of pipeline
        print(output + " ")
        None
      }
    }.toVector
      .sortWith(_._4 < _._4)
      .map(x => ActiveNode(x._1, x._2, x._3)) ++ notForThisTime
    if (a.isEmpty) {
      None//finished
    } else {
      runActiveNodes(a, links, currentTime+1)
    }
  }

  /**
   * output nodes (or endpoints) of the given node
   * @param node node to check the outputs
   * @param links links in network
   * @return list of out points in network. None if is the last one.
   */
  def nextNode(node:Node, links:Map[Node, Vector[(Node, Int)]]): Option[Vector[(Node, Int)]] = {
    links.get(node)
  }
}
