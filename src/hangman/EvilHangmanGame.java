package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.Scanner;
import java.util.*;


public class EvilHangmanGame implements IEvilHangmanGame{

  private Set<String> wordSet = new HashSet<String>();
  private Set<String> theNewWordSubset = new HashSet<String>();
  private SortedSet<Character> guessLettersByPlayer = new TreeSet<>();
  HashMap<String, Set<String>> myMapPartitioner = new HashMap<>(); // String subsetKey and Set<String> words in the list that have the subsetted key word.

  private String theWinnerString = "";
  private String currentSubsetKey;
  private int guessingWordLength = 0;
  @Override
  public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
    wordSet.clear();
    guessLettersByPlayer.clear();
    guessingWordLength = wordLength;
    theWinnerString = "";

    Scanner myScanner = new Scanner(dictionary);

    if(dictionary.length() == 0){
      throw new EmptyDictionaryException();
    }

    while(myScanner.hasNext()) {
      String wordInDictionary=myScanner.next();
      if(wordInDictionary.length() == wordLength){
        wordSet.add(wordInDictionary);
      }
    }
    myScanner.close();


    if(wordSet.size() == 0){
      throw new EmptyDictionaryException();
    }


//    System.out.println("these are all the words in the file printed out from a set: \n");
//    for (String str : wordSet) {
//      System.out.println(str);
//    }
  }

  @Override
  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {

    myMapPartitioner.clear();

    guess = Character.toLowerCase(guess);

    if(guessLettersByPlayer.contains(guess)){
      throw new GuessAlreadyMadeException();
    }
    String subsetKey;
    for (String str : wordSet) {
      subsetKey = getSubsetKey(str, guess);
      if(!myMapPartitioner.containsKey(subsetKey)){
        myMapPartitioner.put(subsetKey, new HashSet<>());
      }
      myMapPartitioner.get(subsetKey).add(str); // get the Hashset from that key.
    }

    String correctOutput = tieBreaker(guess);

//    System.out.println("These are the key-value pairs in the Map: \n");
//    for(Map.Entry <String,Set<String>> mp: myMapPartitioner.entrySet()){
//      System.out.println(mp.getKey() + " " + mp.getValue());
//    }

    guessLettersByPlayer.add(guess);
    wordSet.clear();
    wordSet = myMapPartitioner.get(correctOutput);
    return myMapPartitioner.get(correctOutput);
  }

  @Override
  public SortedSet<Character> getGuessedLetters() {
    return guessLettersByPlayer;
  }


  public String getSubsetKey(String word, char guessedLetter){
    // iterate through each char in the word
    // for each word, we calculate the subsetKey
    // AKA it will return the correct word in respect to the guessedLetter.
    for(int i = 0; i < word.length(); i++){
      if(word.charAt(i) != guessedLetter && !guessLettersByPlayer.contains(word.charAt(i))){
        word = word.replace(word.charAt(i), '_');
      }
    }
    return word;
  }

  private String tieBreaker(char guess) {
    // 1. choose the group with no letter at all. (for ex: ____)
    // 2. if the group has the guess letter, choose the group with the smallest amount of guessed letters
    // 3. choose the one with the rightmost guessed letter (for ex: e_e_ or __ee, the second would be chosen)
    // 4. if there is STILL more than one group, choose the one with rightmost letter and continue doing this until 1 group has been chosen.


    int keySize = 0;
    int partitionCounter = 0;
//    String winnerString="";

    for (String key : myMapPartitioner.keySet()) {
      if(myMapPartitioner.get(key).size() == keySize){
        partitionCounter++;
      }
      if (myMapPartitioner.get(key).size() > keySize) {
        keySize = myMapPartitioner.get(key).size();
        theWinnerString = key;
        partitionCounter = 1;
      }
    }

    if(partitionCounter == 1){
      return theWinnerString;
    }

    // tie breaker 1
    for(String key: myMapPartitioner.keySet()) {
      if (myMapPartitioner.get(key).size() == keySize) {
        if(key.indexOf(guess) == -1){ // its all equal to -----
          theWinnerString = key;
          return theWinnerString;
        }
      }
    }

    // tie breaker 2
    int count = guessingWordLength;
    int smallestLetterCount = 0;
    for(String key: myMapPartitioner.keySet()){
      int letterCount = key.length() - key.replaceAll(String.valueOf(guess), "").length();
      if(letterCount == count){
        smallestLetterCount ++;
      }

      if (letterCount < count){
        smallestLetterCount = 1;
        count = letterCount;
        theWinnerString = key;
      }
    }

    if (smallestLetterCount ==1){
      return theWinnerString;
    }


    // tie breaker 3
    int largestKeyValue = 0;
    for(String key: myMapPartitioner.keySet()) {
      int largestIndex=0;
      for (int i=0; i < key.length(); i++) {
        if (key.charAt(i) == guess) {
          largestIndex += i + 1;
        }
      }
      if(largestIndex > largestKeyValue){
        largestKeyValue = largestIndex;
        theWinnerString = key;
      }
    }

    return theWinnerString;
  }

  public String winnerString(){
    return theWinnerString;
  }

  @Override
  public String getCurrentSubsetKey() {
    return currentSubsetKey;
  }
}
