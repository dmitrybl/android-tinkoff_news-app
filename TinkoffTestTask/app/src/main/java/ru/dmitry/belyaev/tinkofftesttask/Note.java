package ru.dmitry.belyaev.tinkofftesttask;

import java.util.Comparator;

/**
 * Created by dmitrybelyaev on 04.05.2018.
 */

public class Note {

    private String text;
    private int id;
    private long milliseconds;
    private String publicationDate;

    public Note(String text, int id, long milliseconds, String publicationDate) {
        this.text = text;
        this.id = id;
        this.milliseconds = milliseconds;
        this.publicationDate = publicationDate;
    }

    public String getText() {
        return  text;
    }

    public int getId() {
        return id;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public static Comparator<Note> getCompByTime() {
        Comparator comparator = new Comparator<Note>() {
            @Override
            public int compare(Note n1, Note n2) {
                if(n1.getMilliseconds() < n2.getMilliseconds()) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
        };
        return comparator;
    }

}

