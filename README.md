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
## Running the game
1. Open Eclipse, and create a new Java project.
2. Right click the newly created src folder.
3. Click Import..
4. Click General -> File System -> Next.
5. Click Browse..
6. Browse to the folder that contains all the content of the repository.
7. Select that folder.
8. Tick all the files, i.e. constants, events, game_engine, img, etc.
9. Click Finish.
10. Open and run Main.java.

&nbsp;
## Authors
* [reubsmull](https://github.com/reubsmull)
* [evanSpendlove](https://github.com/evanSpendlove)
* [CalNolan](https://github.com/CalNolan)

&nbsp;

## Sprint 1 - Short Documentation
### Requirements:
#### UI-wise:
- [x] Board Panel displaying the game.
- [x] Board Panel with number of the pips.
- [x] Board Panel with room for doubling cube and match score.
- [x] Information Panel displaying game information.
- [x] Command Panel allowing users to enter commands.
- [x] Command Panel echo whatever user enters to information panel (NOTE: whitespace not accepted).
- [x] Command Panel terminate application on input quit.

#### Game-wise:
- [x] Board with initial position of players' checkers.
- [x] Board with ability to move checkers around the board, bear-off, bear-on, get hit via mouse or keyboard.

&nbsp;
### Who did what
see [Trello](https://trello.com/b/A4LqsqAB/backgammon)

| Reuben        | Evan          | Cal           |
| ------------- | ------------- | ------------- |
|     |  |
|      |       |
|       |
****************
Additionally, we also did
- Initialize dice and alternating rolling die between two players
- Additional space for player info
- Home and jail
- Highlighting pips when clicked
- Inform player of result of moving checkers and rolling die
- "Stacking" checkers up so they don't go over the pips
- Ignore player input if it is empty or only contains whitespace
- All our commands start with "/", but simply typing the keyword also works
- Save (or auto save before quitting) contents of info panel to text file
- Prompt player to confirm when quitting game

&nbsp;
### How we did it as a whole:
- **_Explore Objected Oriented Design concepts and techniques_**, to learn how to maintain our code. This is why we modularized and objectified almost everything in the application, this can be seen as a bit too much classes, but when it comes down to maintaining, you will know exactly where the problem is (i.e which class contains the culprit).

- **_Separate interactions between objects_**. With OOP, comes the interaction between objects. For two objects to interact with each other, a class is used to facilitate the interaction, this class is called the Controller class.

  The higher up the interaction chain, the more interaction there is, to the point where the top class handles all the interaction between all the objects. The lower down you go, the less interaction there is, to the point where the objects interact with themselves (i.e. initialize, set and get their own instance variables).

  There are many controller classes used for the interaction between objects in this application. i.e. MainController (interaction between GameController, CommandPanel, InfoPanel, RollDieButton), GameController (Bar, Home, Board, UserPanels), Board (Points, Dices, Checkers), Point (Checkers), Bars (Bar), HomePanel (Home), etc.

  If there's a bug in the code, what we do is understand how the objects interact with each other, then go to the class file that handles the interaction, then work our way down the interaction chain to find the bug.

  For instance, for some reason, we typed /move 1 2, a black checker is at point 2, a white checker is at point 1, black checker is supposed to get hit and go to bar. Command is entered, but the black checker doesn't move. We know that /move commands relies on *CommandPanel* to work, relays system messages through *InfoPanel*, and moves are made through *Board*. The code of /move is definitely in MainController, which it is. Then you work your way down from MainController, to GameController or Board, or wherever to fix the bug, relying heavily on Java's error stack trace.
  
