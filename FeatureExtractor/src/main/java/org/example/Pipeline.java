package org.example;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Pipeline {
    private static StanfordCoreNLP pipeline;
    private static String propertiesName = "tokenize, ssplit, pos";
    private static final Properties properties;

    private Pipeline(){}

    static {
        properties = new Properties();
        properties.setProperty("annotators", propertiesName);
    }

    public static StanfordCoreNLP getPipeline(){
        if(pipeline == null){
            pipeline = new StanfordCoreNLP(properties);
        }
        return pipeline;
    }

    public static String getPropertiesName() {
        return propertiesName;
    }

    public static void setPropertiesName(String newProperties) {
        propertiesName = newProperties;
    }
}
