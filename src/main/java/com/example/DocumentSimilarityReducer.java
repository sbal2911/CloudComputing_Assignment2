package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    // Creation of Map datstructure to store the words which appear in each document
    private Map<String, Set<String>> documentWords = new HashMap<>();

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String word = key.toString();

        for (Text val : values) {
            String doc = val.toString();
            documentWords.computeIfAbsent(doc, k -> new HashSet<>()).add(word);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> documents = new ArrayList<>(documentWords.keySet());
        List<String> outcome = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            for (int j = i + 1; j < documents.size(); j++) {
                String d1 = documents.get(i);
                String d2 = documents.get(j);

                Set<String> wordsList1 = documentWords.get(d1);
                Set<String> wordsList2 = documentWords.get(d2);

                // Intersection computation
                Set<String> common = new HashSet<>(wordsList1);
                common.retainAll(wordsList2);

                // Union computation
                Set<String> join = new HashSet<>(wordsList1);
                join.addAll(wordsList2);

                double jaccardSim = join.size() == 0 ? 0.0 : (double) common.size() / join.size();

                // Ensure alphabetical ordering by DocID
                String first = d1.compareTo(d2) < 0 ? d1 : d2;
                String second = d1.compareTo(d2) < 0 ? d2 : d1;

                // Formatted outcome
                String output = first + ", " + second + " Similarity: " + String.format("%.2f", jaccardSim);
                outcome.add(output);
            }
        }
        Collections.sort(outcome);

        // Writing final sorted results
        for (String result : outcome) {
            context.write(null, new Text(result));
        }
    }
}
