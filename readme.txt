# Scrabble

Group project to design a Java-based Scrabble replica with a JavaFX front-end.

&nbsp;
## About Scrabble
see [Wikipedia](https://en.wikipedia.org/wiki/Scrabble)
> Scrabble is a word game in which two to four players score points by placing tiles, each bearing a single letter, onto a game board divided into a 15×15 grid of squares. The tiles must form words that, in crossword fashion, read left to right in rows or downward in columns, and be included in a standard dictionary or lexicon.

&nbsp;
## IDEs & Packet Managers

For this project, we used IntelliJ IDEA as our IDE and Maven for package management.

- [Downloading and using IntelliJ IDEA](https://www.jetbrains.com/idea/)

&nbsp;
## Running the Unit-Tests.

Prerequisites:
- Java 11
- JUnit 5
- IDE

Using IntelliJ:
1. Create new project using blank template.
2. Set up the folder directory in the form: 
	src -> main
		-> java
			-> scrabbleGame
	    -> test
		-> java
3. Mark the src/main/java as a blue 'Sources root' in the Module Settings for the project.
4. Mark the src/test/java as a green 'Test root' in the Module Settings for the project.
5. Download the repo as a .ZIP file.
6. Copy the .java class files into the src/main/java folder.
7. Copy the .java test files into the src/main/test folder.
8. Now, ensure the project is set to JDK 11.
9. Right click on the 'test' folder and click "Run all 'Tests'".

&nbsp;
## Authors
* [reubsmull](https://github.com/reubsmull)
* [evanSpendlove](https://github.com/evanSpendlove)
* [CalNolan](https://github.com/CalNolan)

&nbsp;

## Sprint 1 - Short Documentation
### Requirements:
- [x] Pool: Stores the value of each tile
- [x] Pool: Stores the tiles currently in the pool
- [x] Pool: Allows the pool to be reset
- [x] Pool: Allows display of the number of tiles in the pool
- [x] Pool: Allows the pool to be check to see if it is empty
- [x] Pool: Allows tiles to be drawn at random from the pool
- [x] Pool: Allows the value of a tile to be queried
- [x] Player: Allows the player data to be reset
- [x] Player: Allows a player's score to be increased
- [x] Player: Allows access to their score
- [x] Player: Allows access to a player's frame (tiles)
- [x] Player: Allows display of a player's name
- [x] Player: Allows the name of the player to be set
- [x] Frame: Stores the letters that each player has in their frame
- [x] Frame: Allows letters to be removed from a frame
- [x] Frame: Allows a check to be made if letters are in the frame
- [x] Frame: Allows a check to be made to see if the frame is empty
- [x] Frame: Allows access to the letters in the frame
- [x] Frame: Allows a frame to be refilled from the pool
- [x] Frame: Allows a frame to be displayed
- [x] PlayerTest: Contains a main method that will run a series of tests on the classes.

&nbsp;
### Who did what
see [Trello](https://trello.com/b/54o8lgQ2/core-progress)

| Reuben        | Evan          | Cal           |
| ------------- | ------------- | ------------- |
| Player Class  |Frame Class    |Pool Class
| Player Test   |Frame Test     |Pool Test
****************

## Sprint 2 - Short Documentation
### Requirements:
- [X] Board Class
- [X] Board Test
- [X] Square Class
- [X] Square Test
- [X] Move Class
- [X] Move Test
- [X] Placement Class
- [X] Placement Test

### Who did what
| Reuben        | Evan          | Cal           |
| ------------- | ------------- | ------------- |
| Board Class (Some Methods)  |Move Class    |Board Class (Some Methods)
| Board Test (Some Methods)   |Move Test     |Board Test (Some Methods)
|               | Placement Class |
| |Placement Test |
| |Square Class  |
| |Square Test  |
****************

## Sprint 3 - Short Docs
### Who did what
| Reuben        | Evan          | Cal           |
| ------------- | ------------- | ------------- |
| Game Engine  |JavaFX (FXML, etc.)   |Scoring
| some Testing   |Some Testing     |Some testing
****************
