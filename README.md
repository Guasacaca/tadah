## Sliding Window (Challenge 1)

Usage in sbt: run \<filename>

The Main class is in RollingWindow


In the future: window size (T = 60 by default) could be passed through arguments. At the moment, it can be changed in
 the file WindowConstants.

##Tasks (Challenge 2).

Usage in sbt: run \<filename>

The Main class is in Tasks
Assumptions made:

1. Links that don't have defined the tasks/nodes related to them, BEFORE the respective link creation, will be ignored.
2. Only first appearance of a task name will be used (e.g. if two or more tasks are named "pepito", only the first
  declaration will be used.
3. One event starts after the last finished (this is just arbitrarily chosen)
4. No cycles are admited. And as the description says, only 1 task is the last one (endpoint).

Everything done in scala 2.11.6

-- Tests are not complete --
