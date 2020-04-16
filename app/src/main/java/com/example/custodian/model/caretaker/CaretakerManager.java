package com.example.custodian.model.caretaker;

import com.example.custodian.R;
import com.example.custodian.model.caretaker.Caretaker;

import java.util.ArrayList;

public class CaretakerManager {

    private static ArrayList<Caretaker> caretakers;

    public static ArrayList<Caretaker> getCaretakers(){
        caretakers = new ArrayList<>();

        caretakers.add(new Caretaker(2, "Roseî Kryskov", "emma@gmail.com", "2 Bedford Crescent", "1990-2-21", "emma", R.mipmap.sitter2,
                "Childcare Certified"));
        caretakers.add(new Caretaker(5, "Adrian Ramos", "adrian@gmail.com", "2 Bedford Crescent", "1991-2-21", "adrian", R.mipmap.sitter3,
                "Been in the game since past 5 years"));
        caretakers.add(new Caretaker(3, "Sommy Ilićic", "rosei@gmail.com", "2 Bedford Crescent", "1989-2-21", "rose", R.mipmap.sitter1,
                "Love to help people"));
        return caretakers;
    }
}
