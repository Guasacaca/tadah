package challenge2

import challenge2.Tasks.Node

/**
 * All the functions used while running through the network
 */
object Network {

  /**
   * An active node is a node that will be executed in this round (time)
   * @param input input event
   * @param node node with action to take
   */
  case class ActiveNode(input:String, node:Node)

  /**
   * Execute the pipeline for a process
   * @param first root task.
   * @param words words to execute
   * @param links links of the graph (network)
   * @return
   */
  def executePipeline(first:Node, words:List[String], links:Map[Node, List[Node]]): (Node, List[String]) = {
    words.length match {
      case 0 =>
        (first, List())
      case _ =>
        val firstRoundActiveNode = ActiveNode(words.head, first)
        runActiveNodes(List(firstRoundActiveNode), links)
        //next word
        executePipeline(first, words.tail, links)
    }
  }

  /**
   * run this level (time) of active nodes
   *
   * @param active active nodes at this level or time.
   * @param links links of the network
   * @return new list of active nodes (next time)
   */
  def runActiveNodes(active: List[ActiveNode], links:Map[Node, List[Node]]): Option[List[ActiveNode]] = {
    //check if any active node is repeated, if so -> concat.
    val concatenateRepeated = active
      .groupBy(_.node)
      .map(x => (x._1, x._2.map(n => n.input).reduce(_.concat(_))) )
      .map(x => ActiveNode(x._2, x._1))
    val a = concatenateRepeated.flatMap{ thisActiveNode =>
      val output = thisActiveNode.node
        .operation
        .execute(thisActiveNode.input)
      //println("output "+output)
      val nexts = nextNode(thisActiveNode.node, links)
      if (nexts.isDefined){
       nexts.get.map(n => ActiveNode(output, n))
      } else {//last node of pipeline
        print(output + " ")
        None
      }
    }.toList
    if (a.isEmpty) {
      None//finished
    } else {
      runActiveNodes(a, links)
    }
  }

  /**
   * output nodes (or endpoints) of the given node
   * @param node node to check the outputs
   * @param links links in network
   * @return list of out points in network. None if is the last one.
   */
  def nextNode(node:Node, links:Map[Node, List[Node]]): Option[List[Node]] = {
    links.get(node)
  }
}
