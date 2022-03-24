# COMP4190 Assignment2-Bayesian network
##Ran Shi(7814643), Siwen Sun(7898970)

```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A2$ tree
.
├── Bayesian.java
├── COMP4190_a2_report.pdf
├── README.md
└── makefile

0 directories, 4 files
```
There is a makefile, please use it to compile my code by
```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A2$ make
javac Bayesian.java
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A2$
```

You can run the code like this
```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A2$ java Bayesian
Question 2-b(i) :======================
P(+fraud) = 0.0043
***************************************

Question 2-b(ii) :=====================
After eliminate Variable : travel
the probability is [0.05401, 0.00083]

After eliminate Variable : oc
the probability is [0.06960, 0.06890]

P(¬fraud | fp, ¬ip, crp) = 0.98502
P( fraud | fp, ¬ip, crp) = 0.01498
***************************************
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A2$
```

At the end, you can use make clean to clear codes
```asm
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A2$ make clean
rm -f *.class
siwen@DESKTOP-61TLL0I:/mnt/c/Users/Administrator/IdeaProjects/COMP419/A2$
```