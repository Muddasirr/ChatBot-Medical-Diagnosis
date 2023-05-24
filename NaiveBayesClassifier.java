import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class NaiveBayesClassifier {
    private static final String WORDNET_PATH = "C:\\Users\\gm computer\\IdeaProjects\\dict";
    private static IDictionary dict;
    public Map<String, Double> classProbabilities;
    public Map<String, Map<String, Double>> featureProbabilities;

    public NaiveBayesClassifier() {
        classProbabilities = new HashMap<>();
        featureProbabilities = new HashMap<>();
    }

    public void train(HashMap<String, ArrayList<String>> trainingData) {

        // Calculate the class probabilities
        int totalPatients = 0;
        for (String disease : trainingData.keySet()) {
            totalPatients += trainingData.get(disease).size();
         //   System.out.print(disease);

        }
        for (String disease : trainingData.keySet()) {
            double probability = (double) trainingData.get(disease).size() / (double) totalPatients;
            classProbabilities.put(disease, probability);
           // System.out.print(disease);
        }

        // Calculate the feature probabilities
        for (String disease : trainingData.keySet()) {
            Map<String, Double> featureCounts = new HashMap<>();
            for (String symptom : trainingData.get(disease)) {
                String[] words = symptom.split(" ");
                for (String word : words) {
                    Double count = featureCounts.get(word);
                    if (count == null) {
                        count = 0.0;
                    }
                    featureCounts.put(word, count + 1.0);
                }
            }

            Map<String, Double> featureProbabilitiesForClass = new HashMap<>();
            for (String word : featureCounts.keySet()) {
                double probability = (featureCounts.get(word) + 1.0) / (double) (trainingData.get(disease).size() + 2.0);
                featureProbabilitiesForClass.put(word, probability);
            }

            featureProbabilities.put(disease, featureProbabilitiesForClass);
        }

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

    public String classify(ArrayList<String> patientSymptoms) {

        Map<String, Double> scores = new HashMap<>();
        for (String disease : classProbabilities.keySet()) {
            System.out.print(disease);
            double score = Math.log(classProbabilities.get(disease));
            for (String symptom : patientSymptoms) {
                String[] words = symptom.split(",");
                for (String word : words) {
                   List<String> wordss= getSynonyms(word);
                   for(String F:wordss){
                    Double probability = featureProbabilities.get(disease).get(F);
                    if (probability != null) {
                        score += Math.log(probability);
                    }
                }
            }
         //   System.out.println(score);
            scores.put(disease, score);
        }}


        String bestDisease = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (String disease : scores.keySet()) {
            double score = scores.get(disease);
            if (score > bestScore) {
                bestScore = score;
                bestDisease = disease;
            }
        }
        return bestDisease;
    }

   }
