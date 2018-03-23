The files containing the graphs are structured as follows:

First line is a header that contains information regarding the maze. The information is as follows:

name of maze
start treeNode for Ms Pac-Man
treeNode that corresponds to the lair
start treeNode for the ghosts
number of nodes in the maze
number of pills in the maze
number of power pills in the maze
number of junctions in the maze

All other lines corresponds to individual nodes on the graph. Each treeNode has the following information:

treeNode index
x-coordinate	
y-coordinate
neighbouring treeNode in UP direction (-1 if none)
neighbouring treeNode in RIGHT direction (-1 if none)
neighbouring treeNode in DOWN direction (-1 if none)
neighbouring treeNode in LEFT direction (-1 if none)
pill-index of the treeNode (-1 if none)
power-pill index of the treeNode (-1 if none)