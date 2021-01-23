package com.romit.dicegame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
public class DicegameApplication {

    public static ArrayList<Integer> numbersDone = new ArrayList<Integer>();

    //Putting Player No. and their score,rank in the map
    public static HashMap<Integer, PlayerScore> hm = new HashMap<>();

    public static ArrayList<Integer> playerWin = new ArrayList();

    public static int endrank=0;

    public static void main(String[] args) {
        SpringApplication.run(DicegameApplication.class, args);

        int points = 10;
        int playerCount=7;

        DicegameApplication obj = new DicegameApplication();
        gameBuilder(points,playerCount);
    }

    public static void gameBuilder(int points, int playerCount){

        int currentPlayers = playerCount;
        // Roll the dice till all players win
        do {
            for (int i = 1; i <= currentPlayers; i++) {

                int newPlayer;
                do {
                    newPlayer = getNextChance(playerCount);

                    // to exclude players already won
                    if(playerWin.contains(newPlayer) )
                        continue;

                    // to miss the next chance for player getting 1 two times in succession
                    if(hm.containsKey(newPlayer) && hm.get(newPlayer).nextChance == false)
                    {
                        numbersDone.add(newPlayer);
                        i++;
                        hm.get(newPlayer).nextChance = true;
                    }

                    if(!numbersDone.contains(newPlayer)){
                        numbersDone.add(newPlayer);
                        break;
                    }

                } while (true);

                System.out.println("Player-" + newPlayer + " its your turn (press ‘r’ to roll the dice)");

                int rollNo = 1 + (int) (Math.random() * ((5) + 1));
                System.out.println("Points obtained in this roll=" + rollNo);

                // When a player rolls 1 twice successively, he misses next chance
                if( hm.containsKey(newPlayer) && rollNo == 1 && hm.get(newPlayer).lastroll ==1 ) {
                    System.out.println("Player " + newPlayer + ", You will miss your next chance as you got 1 two times consecutively.");
                    hm.get(newPlayer).nextChance=false;
                }else if( hm.containsKey(newPlayer) )
                    hm.get(newPlayer).nextChance=true;

                int netRoll=0;

                // When a player rolls 6, he gets another chance to roll
                if(rollNo == 6){
                    System.out.println("Player " +newPlayer+", You get another Chance as you rolled 6");
                    rollNo = 1 + (int) (Math.random() * ((5) + 1));
                    netRoll = rollNo + 6;
                }else
                    netRoll = rollNo;

                if (hm.containsKey(newPlayer)) {
                    PlayerScore playerScore = hm.get(newPlayer);
                    playerScore.lastroll=rollNo;
                    playerScore.score = hm.get(newPlayer).score + netRoll;
                    netRoll = playerScore.score;
                    //hm.put(nextPlayer,playerScore);
                } else {
                    PlayerScore playerScore = new PlayerScore();
                    playerScore.lastroll=rollNo;
                    playerScore.score = netRoll;
                    hm.put(newPlayer, playerScore);
                }

                if (netRoll >= points) {
                    endrank++;
                    hm.remove(newPlayer);
                    findNewRank(playerCount);
                    System.out.println("Player " + newPlayer + " won and his/her current rank is " + endrank);
                    playerWin.add(newPlayer);

                } else {
                    findNewRank(playerCount);
                }
            }

            currentPlayers = playerCount - playerWin.size();

            for (Map.Entry<Integer, PlayerScore> i : hm.entrySet()) {
                System.out.print("Player" + i.getKey() + ", Score="+i.getValue().score+" and Rank= " + i.getValue().rank);
            }

            numbersDone.removeAll(numbersDone);

        }while (playerWin.size() != playerCount) ;
        //numbers.stream().filter().
    }

    public static void findNewRank(int playerCount){

        // iterating using for loop.
        for (Map.Entry<Integer, PlayerScore> i : hm.entrySet()) {
            int r = 1, s = 1;

            for (Map.Entry<Integer, PlayerScore> j : hm.entrySet())
            {
                if (j != i && j.getValue().score > i.getValue().score)
                    r += 1;

                if (j != i && j.getValue().score == i.getValue().score)
                    s += 1;
            }

            // Use formula to obtain  rank
            i.getValue().rank = (r + (s - 1) / 2);

        }
    }

    public static int getNextChance(int playerCount){
        Random rand = new Random();
        int nextPlayer = rand.ints(playerCount, 1,playerCount+1)
                //.filter(num -> (!numbersDone.contains(num) || !playerWin.contains(num)))
                .limit(1).sum();

        return nextPlayer;
    }

    static class PlayerScore{
        int score;
        int rank;
        int lastroll;
        boolean nextChance;
    }

}




