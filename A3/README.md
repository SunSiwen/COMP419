# COMP4190 Assignment3-RL

## Ran Shi(7814643), Siwen Sun(7898970)

```asm
[suns5@kingfisher ~/419A3]> tree
.
├── A3.java
├── GridConf.txt
├── makefile
├── QLearningAgent.java
├── README.md
├── results.txt
└── ValueIterationAgent.java

0 directories, 7 files
[suns5@kingfisher ~/419A3]>
```
Here are two file called GridConf.txt and results.txt.

I just use them to test and will not submit them

There is a makefile, please use it to compile my code by

```asm
[suns5@kingfisher ~/419A3]> make
javac A3.java
[suns5@kingfisher ~/419A3]>
```

You can run the code like this

The command consits of 5 part

the 1st and 2nd part is easy to understand.

the 3rd file is the path of GridConf.txt

the 4th file is the path of results.txt

the 5th part is a double type, I use epsilon-greedy in my Q-learning. the num should inside [0,1];

Normally, this value is very small. (For example: 0.15 means 15% and 0.01 means 1%)
```asm
[suns5@kingfisher ~/419A3]> java A3 GridConf.txt results.txt 0.15
1,0,78,MDP,stateValue: 0.43
1,0,78,MDP,bestPolicy: Go West
1,0,29,RL,bestQValue: 0.19
1,0,29,RL,bestPolicy: Go West
[suns5@kingfisher ~/419A3]>
```

At the end, you can use make clean to clear codes

```asm
[suns5@kingfisher ~/419A3]> make clean
rm -f *.class
[suns5@kingfisher ~/419A3]>
```