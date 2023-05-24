import com.swabunga.spell.engine.GenericSpellDictionary;
import com.swabunga.spell.event.SpellChecker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import edu.mit.jwi.item.*;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import java.net.URL;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;

public class Chatbot {
    NaiveBayesClassifier classifier = new NaiveBayesClassifier();
    //    Path to the Dict folder i have sent
    private static final String WORDNET_PATH = "C:\\Users\\gm computer\\IdeaProjects\\dict";

    private static IDictionary dict;

    //    Path set to the enigma txt that is already in the Src
    private static final File file = new File("C:\\Users\\gm computer\\IdeaProjects\\AI\\AI\\src\\engmix.txt");
    private final SpellChecker spellChecker = new SpellChecker(new GenericSpellDictionary(file));
    ArrayList<String> Patient = new ArrayList();


    HashMap<String ,ArrayList<String>> Data = new HashMap<String, ArrayList<String>>();

    public void insert() throws FileNotFoundException {


        // read the data from the file
        File F= new File("C:\\Users\\gm computer\\IdeaProjects\\AI\\AI\\src\\Symptoms.txt");
        Scanner scanner = new Scanner(F);
        String currentDisease = new String();
        ArrayList<String> currentSymptoms = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.endsWith(":")) {
                // found a new disease, create a new ArrayList for its symptoms
                currentDisease = line.substring(0, line.length() - 1);
                currentSymptoms = new ArrayList<>();
                Data.put(currentDisease, currentSymptoms);
            } else {
                // found a symptom, add it to the current ArrayList
                currentSymptoms.add(line);
            }
        }
        scanner.close();

       //  print the contents of the HashMap
        //for (String disease : Data.keySet()) {
          //  System.out.println(disease + ": " + Data.get(disease));
       // }
    }




    public Chatbot() throws IOException {

        insert();

        classifier.train(Data);
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Mr. Doctor");
        System.out.println("Kindly Enter your Symptoms");
             while (sc.hasNextLine()) {
            String input = sc.nextLine();
            List<String> list = getMisspelledWords(input);
                if (!list.isEmpty()) {
                    System.out.println("I am sorry i am unable to understand the following words");
                    System.out.println(list);
                    System.out.println("");
                    System.out.println("Please type you message again"); }
               else{ if (input.equalsIgnoreCase("Done")) break;
                 Patient.add(input);}

            }

        System.out.print("Based on your symptoms and the tests we've run, I suspect that you have ");
        System.out.print(classifier.classify(Patient));
    }




    public void search() {
        int i=0;
        for (String disease : Data.keySet()) {
            //System.out.println(disease + ": " + Data.get(disease));
            ArrayList<String> F = Data.get(disease);
            i=0;
            for(String S: F){

                for(String m:Patient){
                    if(S.contains(m)){
                        i++;
                        if(i==Patient.size()-1) {
                            System.out.print(disease);
                            System.exit(0);}

                    }
                }
            }}}

    static {
        URL url = null;
        try {
            url = new URL("file", null, WORDNET_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(url.getFile());
        dict = new edu.mit.jwi.Dictionary(file);
        try {
            dict.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getSynonyms(String word) {
        List<String> synonyms = new ArrayList<>();
        WordnetStemmer stemmer = new WordnetStemmer(dict);
        List<String> stems = stemmer.findStems(word, null);
        for (String stem : stems) {
            POS pos = getPartOfSpeech(stem);
            if (pos != null) {
                IIndexWord indexWord = dict.getIndexWord(stem, pos);
                if (indexWord != null) {
                    List<IWordID> wordIDs = indexWord.getWordIDs();
                    for (IWordID wordID : wordIDs) {
                        ISynset synset = dict.getWord(wordID).getSynset();
                        List<IWord> synsetWords = synset.getWords();
                        for (IWord synsetWord : synsetWords) {
                            String lemma = synsetWord.getLemma();
                            synonyms.add(lemma);
                        }
                    }
                }
            }
        }
        return synonyms;
    }

    private static POS getPartOfSpeech(String word) {
        if (dict.getIndexWord(word, POS.NOUN) != null) {
            return POS.NOUN;
        } else if (dict.getIndexWord(word, POS.VERB) != null) {
            return POS.VERB;
        } else if (dict.getIndexWord(word, POS.ADJECTIVE) != null) {
            return POS.ADJECTIVE;
        } else if (dict.getIndexWord(word, POS.ADVERB) != null) {
            return POS.ADVERB;
        } else {
            return null;
        }
    }

    public List<String> getMisspelledWords(String text) {
        List<String> misspelledWords = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(text);
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            if (!spellChecker.isCorrect(word)) {
                misspelledWords.add(word);
            }
        }
        return misspelledWords;
    }


    public static void main(String[] args) throws IOException {
        Chatbot chatbot = new Chatbot();




    /*    String sampleText = "Thas cat is walking to werk.";
        List<String> misspelledWords = chatbot.getMisspelledWords(sampleText);

        System.out.println("Misspelled words: " + misspelledWords);

        System.out.println();
        System.out.println();

        String word = "Cold";
        List<String> synonyms = chatbot.getSynonyms(word);
        System.out.println("Synonyms of " + word + ":");
        for (String synonym : synonyms) {
            System.out.println(synonym);
        }*/
    }

}
