package utils;

import java.util.Map;

public class DataRow {
    private final Map<String, Double> attributes;

    public DataRow(Map<String, Double> attributes) {
        this.attributes = attributes;
    }

    public Double getValue(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
