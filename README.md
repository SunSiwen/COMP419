# COMP419
```
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ tree
.
├── backtrack.java
├── makefile
├── puzzles.txt
└── test.txt

0 directories, 4 files
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ make
javac backtrack.java
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ java backtrack H1

please give me a puzzle(File path):
test.txt








# Solution
1 b 1 1 b 2
1 _ 0 _ _ b
b _ 2 b _ _
2 _ b 3 _ 0
b 1 3 b 1 0
1 _ b _ 0 0
THERE ARE 20 TREE NODES GENERATED








# Solution
b _ 0 _ b _
1 0 0 0 2 0
0 0 _ 1 b _
_ 0 _ 0 3 b
b 2 b 3 b 3
1 0 2 b 3 b
THERE ARE 37 TREE NODES GENERATED








# Solution
1 1 b 3 b 2
b 2 2 b 4 b
3 b 2 _ b 2
b 3 b _ 2 0
1 0 _ 2 b _
0 0 _ b 3 b
THERE ARE 55 TREE NODES GENERATED








# Solution
b _ 0 0 2 b
1 1 0 _ b 3
1 b 1 1 _ b
1 1 _ b _ 2
b _ 1 1 2 b
_ 1 b 2 b _
THERE ARE 74 TREE NODES GENERATED








# Solution
b _ _ 1 _ b
2 0 _ b 1 _
b _ 1 1 1 0
_ 2 b 2 b 2
2 b 2 1 2 b
b 2 1 b _ 1
THERE ARE 93 TREE NODES GENERATED








# Solution
b 2 1 b _ _
_ b 1 3 b 1
0 1 2 b 2 _
0 _ b 3 1 b
_ 0 2 b 2 1
b _ _ 2 b 1
THERE ARE 112 TREE NODES GENERATED


please give me a puzzle(File path):
quit

Program completed normally.
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ make clean
rm -f *.class
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A1$ ls
backtrack.java  makefile  puzzles.txt  test.txt
```