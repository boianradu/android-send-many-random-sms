package ep.radu.don.sendmanyrandomsms;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static android.R.id.input;

/**
 * Created by ep on 29/12/2017.
 */

public class RandomStrings {

    private static Random rand = new Random();
    private static Scanner input = new Scanner(System.in);
    private String LOGTAG = "RADUEP";
    List<String> words = new ArrayList<>();
    List<String> complements = new ArrayList<>();
    List<String> punctuations = new ArrayList<>();
    List<String> emoticons = new ArrayList<>();

    RandomStrings(String defaultString, String defaultComplements, String defaultPunctuation, String defaultEmoticons) {
        Log.e(LOGTAG, defaultString);
        try {
            Log.i(LOGTAG, defaultString);
            String[] wordsString = defaultString.split(" ");
            for (String word : wordsString) {
                words.add(word);
            }

            String[] wordsComplements = defaultComplements.split(" ");
            for (String complement : wordsComplements) {
                complements.add(complement);
            }

            String[] wordPunctuations = defaultPunctuation.split(" ");
            for (String punctuation : wordPunctuations) {
                punctuations.add(punctuation);
            }

            String[] wordEmoticons = defaultEmoticons.split(" ");
            for (String emoticon : wordEmoticons) {
                emoticons.add(emoticon);
            }
        } catch (Exception e) {
            Log.e(LOGTAG, "Try " + e);
            e.printStackTrace();
        }
    }

    public String generatePhrase() {
        int wordNum = words.size(),
                punctuationNum = punctuations.size(),
                emoticonNum = emoticons.size(),
                complementNum = complements.size();

        int place;
        String finalPhrase = "";
        for (int indexPunctuation = 0; indexPunctuation < 3; indexPunctuation++) {
            for (int i = 0; i < 5; i++) {
                place = rand.nextInt(wordNum);
                String s = words.get(place);
                finalPhrase += s + " ";
                if ((indexPunctuation * 3 + i) % 2 == 0 || (indexPunctuation * 3 + i) % 5 == 0) {
                    place = rand.nextInt(complementNum);
                    String sC = complements.get(place);
                    finalPhrase += sC + " ";
                }


                if ((indexPunctuation * 3 + i) % 2 == 5 || (indexPunctuation * 3 + i) % 9 == 0) {
                    place = rand.nextInt(emoticonNum);
                    String sE = emoticons.get(place);
                    finalPhrase += "(" + sE + ") ";
                }
            }

            place = rand.nextInt(punctuationNum);
            String sP = punctuations.get(place);
            finalPhrase += sP + " ";
        }

        place = rand.nextInt(wordNum);
        String s = words.get(place);
        finalPhrase += s + "!";
        return finalPhrase;
    }
}
