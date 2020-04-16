package com.example.custodian.model.caretaker;

import com.example.custodian.model.caretaker.Caretaker;

import java.util.ArrayList;
import java.util.List;

public class Hired {
    private ArrayList<String> date_of_hire;
    private List<Caretaker> caretaker;

    Hired() {
    }

    public Hired(boolean init) {
        date_of_hire = new ArrayList<>();
        caretaker = new ArrayList<>();
    }

    public List<String> getDate_of_hire() {
        return date_of_hire;
    }

    public void addDate_of_hire(String date_of_hire) {
        this.date_of_hire.add(date_of_hire);
    }

    public List<Caretaker> getCaretaker() {
        return caretaker;
    }

    public void addCaretaker(Caretaker caretaker) {
        this.caretaker.add(caretaker);
    }
}
