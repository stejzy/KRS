package utils;

import java.util.Map;

public class DataRow {
    private final Map<String, Object> attributes;

    public DataRow(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Double getNumericValue(String attributeName) {
        Object value = attributes.get(attributeName);
        return value instanceof Number ? ((Number) value).doubleValue() : null;
    }

    public String getStringValue(String attributeName) {
        Object value = attributes.get(attributeName);
        return value instanceof String ? (String) value : null;
    }

    public Object getValue(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public String toString() {
        return attributes.toString();
    }
}
