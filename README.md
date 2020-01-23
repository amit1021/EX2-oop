# The Maze of Waze Ex3  

This assignment is based on EX2.  
The main purpose of the assignment is to develop game logic.  
Where a group of robots has to perform movement tasks (fruit collection) on a weighted graph.  

**package dataStructure (Taken from Ex2):**

**nodeData: this class implements node_data interface.
nodeData represents the set of operations applicable on a node (vertex) in a (directional) weighted graph.

**edgeData: this class implements edge_data interface.
edgrData represents the set of operations applicable on a directional edge(src,dest) in a (directional) weighted graph.

**DGraph: this class implemens graph interface.
DGraph represents a directional weighted graph, should support a large number of nodes (over 100,000).  

**package algorithems(Taken from Ex2):**

Graph-Algo: this class implements graph-algorithems interface.
Graph-Algo represents the "regular" Graph Theory algorithms.  

**package gameClient:**  

**comperator: comperator between fruit value.

**Fruit: Fruit represents the set of operations applicable on a Fruit in the game.

**Robot: Robot represents the set of operations applicable on a Robot in the game.

**initiateGame: initiate the graph, Fruits and Robots of the game.

**MyGameGui: this class responsible for the presentation of the graph, Robots and Fruits.   
start the two type of the game. 

**Utils: all the help function in the various classes. 

**KML_Logger: this class allows to export the graph,Robots and Fruits to a KML file.  

**HashMapComperator: this class is designed to compare values in HashMap to find the best scores in the game.

