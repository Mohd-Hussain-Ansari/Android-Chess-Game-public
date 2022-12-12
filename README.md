# Android-Chess-Game
My T.Y B.Sc semester 6 project

## Screenshots

#### Main Screen
 ![Main Screen](https://user-images.githubusercontent.com/103325822/207079997-999928f4-516c-40c9-8b83-1ee5fce34b49.jpg)

#### Match Settings 
![image-160](https://user-images.githubusercontent.com/103325822/207081812-511ea193-1657-4ba2-a417-d9092217a936.jpg)

#### Game Screen
![image-112](https://user-images.githubusercontent.com/103325822/207081287-f2744f11-1eb2-4c42-bf97-4087299110eb.jpg)

#### Play With Friend Screen
![image-133](https://user-images.githubusercontent.com/103325822/207083143-04aa47aa-002f-4df6-bcd8-d3ccfe0c2555.jpg)

A full demo of the app is available [here.](https://drive.google.com/file/d/1j1EAnUxVJeEeLa8wYh7tTzymF7V_Hbsj/view?usp=share_link)

## Details

- This game was built in 2-Dimensional.
- This software will check all the movements done by the 
player in the game & also suggest valid moves for a good understanding of the game.
- Moves are recorded for future reference.
- One player can play with a computer also.
- All players can see the last piece moved.
- A player can add a timer in the game also.
- There are also undo redo features in the game.
- There is a total of four kinds of draw in the game.
  1. Stalemate.
  2. Repeated move.
  3. Insufficient materials.
  4. fifty moves draw.
- The game will end in the following ways.
  1. When one of the players will win the match by 
  checkmate.
  2. When one of the players will lose the match by out of 
  time due to the timer.
  3. Draw between the players.
  4. When one of the players will resign and wins go the 
  other player.
- The game will save automatically when the player by mistake closes 
the game.
- The player can also save the game whenever he/she wants until 
a minimum of 3 moves are played in the game.
- A player can play with another player online as well as 
offline.

### Online features

- Players can sign in using their Gmail account.
- One room is created with a room name and password for the player who wants to play with a friend.
- A player can play one move in priority.
  * This feature was added for the player who has less time left but they have a good move to win.

For more details please find my college project documentation [here.](https://drive.google.com/file/d/1ZZenRa138_Z-R4udcf-Nk1pvGKb48q8u/view?usp=share_link)

## Firebase

Play online will not work without Firebase connection.

To enable firebase connection follow following procedure:
- Replace [google-services.json](app/google-services.json) file with your file.
- Enable Google sign in function in firebase from sign in method of authentication. 
