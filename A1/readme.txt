# COMP4190 Assignment1-CSP
##Ran Shi(7814643), Siwen Sun(7898970)

How to test my code? See the structure by tree
```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ tree
.
├── Constrain.java
├── Factory.java
├── HybridFactory.java
├── Investigation.txt
├── MostConstrainedNodeFactory.java
├── MostConstrainingNodeFactory.java
├── Node.java
├── PlaceConstrain.java
├── readme.txt
├── WallConstrain.java
├── backtrack.java
├── forward_checking.java
├── makefile
├── testQ1.txt
└── testQ2.txt

0 directories, 15 files
```
There is a makefile, please use it to compile my code
```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ make
javac backtrack.java
javac forward_checking.java
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$
```
You can put many puzzles into a single file.

###part1 backtrack
The code is backtrack.class which needs the selection of Heuristics as the parameter.
There are 3 selections(H1, H2 and H3). You can do like this
```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ java backtrack H1

Please give me a puzzle(File path):
testQ1.txt

Received input. Start processing


# Solution
1b11b2
1_0__b
b_2b__
2_b3_0
b13b10
1_b_00
THERE ARE 20 TREE NODES GENERATED
SPEND 25MS WITH 17 WALLS

# Solution
11b3b2
b22b4b
3b2_b2
b3b_20
10_2b_
00_b3b
THERE ARE 18 TREE NODES GENERATED
SPEND 2MS WITH 19 WALLS

.
.
There are too many lines, so I just put the start and the end here
.
.

# Solution
_b_0_01b3b
__b3b10_b_
_03b2___1_
__b30_b__1
b13b2b__1b
_2b_0_1__1
_b2_0_b3b_
_2010_3b4b
2b3b_2b4b3
b3b2_b3b3b
THERE ARE 145 TREE NODES GENERATED
SPEND 3MS WITH 40 WALLS

Please give me a puzzle(File path):


```

After that, you could enter another file path or exit by ctrl+C
###part2 forward_checking
The same with part 1 and the code name is forward_checking.class

and run
```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ java forward_checking H1

Please give me a puzzle(File path):
testQ1.txt

Received input. Start processing


# Solution
1b11b2
1_0__b
b_2b__
2_b3_0
b13b10
1_b_00
THERE ARE 20 TREE NODES GENERATED
SPEND 37MS WITH 17 WALLS

# Solution
11b3b2
b22b4b
3b2_b2
b3b_20
10_2b_
00_b3b
THERE ARE 18 TREE NODES GENERATED
SPEND 3MS WITH 19 WALLS

.
.
There are too many lines, so I just put the start and the end here
.
.

# Solution
_b_0_01b3b
__b3b10_b_
_03b2___1_
__b30_b__1
b13b2b__1b
_2b_0_1__1
_b2_0_b3b_
_2010_3b4b
2b3b_2b4b3
b3b2_b3b3b
THERE ARE 62 TREE NODES GENERATED
SPEND 3MS WITH 40 WALLS

Please give me a puzzle(File path):

```
After that, you could enter another file path or exit by ctrl+C


At the end, you can use make clean to clear codes
```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ make clean
rm -f *.class
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$
```