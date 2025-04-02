package org.example;

import java.io.Serializable;

public class Document implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String place;
    private final String body;

    public Document(String place, String body) {
        this.place = place;
        this.body = body;
    }

    public String getPlace() {
        return place;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Document:\n" +
                "place = " + place + "\n" +
                "body = " + body + "\n" +
                '}';
    }
}
