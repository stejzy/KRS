package org.example;

import java.io.Serial;
import java.io.Serializable;

public class Document implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String place;
    private final int index;
    private final String body;

    public Document(String place, String body, int index) {
        this.place = place;
        this.body = body;
        this.index = index;
    }

    public String getPlace() {
        return place;
    }

    public String getBody() {
        return body;
    }

    public int getIndex() {return index;}

    @Override
    public String toString() {
        return "Document:\n" +
                "place = " + place + "\n" +
                "index = " + index + "\n" +
                "body = " + body + "\n" +
                '}';
    }
}
