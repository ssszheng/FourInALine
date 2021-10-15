## four-in-line game 

A two-player connection game in which the players ﬁrst choose a color and then take turns
dropping colored discs into a seven-column, six-row, vertically suspended grid. The pieces fall
straight down, occupying the next available space within the column. The object of the game is
to connect four of one's own discs of the same color next to each other vertically, horizontally, or
diagonally before your opponent. Four in a Line is a strongly solved game. The ﬁrst player can
always win by playing the right moves.

- Folders:

  - src contains the Java source files.
  - testdata contains the board data files needed for testing.
  - libs has the JUnit jar for running tests on the command line.
  - \tests.GameTest contains the JUnit tests for the four-in-line game.

- Running the program:

  - The main method is located in Game.java (right click -> run As -> Java Application )

  - Tests are located under (src/tests/)
  To run the tests: Right click on GameTest.java, run As -> JUnit tests)


- Running the application from commandline/terminal:

  - To compile and run the program from commandline/terminal: 
  javac -cp libs/junit-platform-console-standalone-1.5.0-RC2.jar -sourcepath src  -d out src/a2/*.java
  java -cp out a2.Game

  - Or run ./run.sh from terminal


  - Running the tests from commandline/terminal:

  - javac -cp libs/junit-platform-console-standalone-1.5.0-RC2.jar -sourcepath src  -d out src/tests/*.java
  java -jar libs/junit-platform-console-standalone-1.5.0-RC2.jar -cp out --scan-classpath --details=tree

  - Or run ./test.sh from terminal
