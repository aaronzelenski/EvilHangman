package hangman;
import java.io.File;
import java.io.*;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.stream.Stream;

public class EvilHangman {

  public static void main(String[] args) throws IOException, EmptyDictionaryException, GuessAlreadyMadeException {
    String dictionaryFileName = "";
    int userWordLength = 0;
    int numGuesses = 0;
    if(args.length != 3) {
      System.out.print("Missing Game Arguments");
    }
    dictionaryFileName = args[0];
    File dictionary = new File(dictionaryFileName);

    userWordLength = Integer.parseInt(args[1]);

    int maxNumberOfWrongGuess = Integer.parseInt(args[2]);


    boolean guessedWord = false;

    String evilWord = "";

    for(int i = 0; i < userWordLength; i++) {
      evilWord += "-";
    }
    EvilHangmanGame myEvilHangmanGame = new EvilHangmanGame();
    try {
      myEvilHangmanGame.startGame(dictionary, userWordLength);
    } catch(Exception e) {
      if(e instanceof EmptyDictionaryException) {
        System.out.print("Empty i or incorrect word length \n");
      }
      else {
        System.out.print("Incorrect input \n");
      }
      return;
    }

    SortedSet<Character> guessLettersByPlayer = new TreeSet<>();
    Set<String> setProducedbyMakeGuess = new HashSet<>();


    while(maxNumberOfWrongGuess != 0 && !guessedWord) {
      String maxKey;

      System.out.println("You have " + maxNumberOfWrongGuess + " guesses left");
      guessLettersByPlayer=myEvilHangmanGame.getGuessedLetters();

      String usedLetters="";
      for (Character c : guessLettersByPlayer) {
        usedLetters += c + " ";
      }

      System.out.println("Used letters: " + usedLetters);
      System.out.println("Word:" + evilWord);

      Scanner input = new Scanner(System.in);
      System.out.print("Enter guess: \n");
      String guessByPlayer = input.nextLine().toLowerCase();

      if (guessByPlayer.length() != 1 || !Character.isLetter(guessByPlayer.charAt(0)) || Character.isWhitespace(guessByPlayer.charAt(0))) {
        System.out.print("Invalid input!\n");
        continue;
      }
      try {
        setProducedbyMakeGuess=myEvilHangmanGame.makeGuess(guessByPlayer.charAt(0));
      } catch (GuessAlreadyMadeException e) {
        System.out.print("Guess already made!\n");
        continue;
      }


      maxKey = myEvilHangmanGame.winnerString();
      StringBuilder word = new StringBuilder(evilWord);

      int count=0;
      for (int i=0; i < userWordLength; i++) {
        if (maxKey.charAt(i) != '_' && maxKey.charAt(i) == guessByPlayer.charAt(0)) {
          count++;
          word.replace(i, i + 1, guessByPlayer);
        }
      }

      evilWord = word.toString();

      if (count == 0) {
        System.out.print("Sorry, there are no " + guessByPlayer + " \n");
        maxNumberOfWrongGuess--;
      } else {
        System.out.print("Yes there is " + count + " " + guessByPlayer + " \n");
        if (!evilWord.contains("-")) {
          System.out.print("You Win! The word was: " + evilWord);
          guessedWord=true;
        }
      }
    }
    if(maxNumberOfWrongGuess == 0) {
      System.out.print("Sorry, you lost! The word was: " + setProducedbyMakeGuess.iterator().next());

    }
  }
}
