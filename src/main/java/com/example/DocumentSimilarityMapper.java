package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {

    private Text wordKey = new Text();
    private Text docIdValue = new Text();

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();  // For DocID capitalization of first letter
        String[] tokens = line.split("\\s+");

        if (tokens.length < 2) return;

        String docCap = tokens[0];
        docIdValue.set(docCap);

        Set<String> uniqueWords = new HashSet<>();
        for (int i = 1; i < tokens.length; i++) {
            String word = tokens[i].toLowerCase().replaceAll("[^a-z0-9]", ""); //For converting all words to lowercase and removing unnecessary punctuation symbols
            if (!word.isEmpty()) {
                uniqueWords.add(word);
            }
        }

        for (String word : uniqueWords) {
            wordKey.set(word);
            context.write(wordKey, docIdValue);
        }
    }
}
