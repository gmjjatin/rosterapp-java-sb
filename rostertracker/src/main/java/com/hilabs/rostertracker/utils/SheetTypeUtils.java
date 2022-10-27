package com.hilabs.rostertracker.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetTypeUtils {
    public static List<String> allTypeList = getAllTypeList();
    public static List<String> dataTypeList = getDataSheetTypeList();
    public static String capitalizeFirstLetter(String name) {
        String firstLetter = name.substring(0, 1);
        String remainingLetters = name.substring(1);
        firstLetter = firstLetter.toUpperCase();
        return firstLetter + remainingLetters;
    }

    public static List<String> getAllPossibleCombos(String str) {
        List<String> combos = new ArrayList<>();
        combos.add(str.toUpperCase());
        combos.add(str.toLowerCase());
        combos.add(capitalizeFirstLetter(str.toLowerCase()));
        return combos;
    }

    private static List<String> getDataSheetTypeList() {
        List<String> list = new ArrayList<>();
        list.addAll(getAllPossibleCombos("TERM"));
        list.addAll(getAllPossibleCombos("DATA"));
        list.addAll(getAllPossibleCombos("NON_TERM"));
        return list;
    }

    private static List<String> getAllTypeList() {
        List<String> list = new ArrayList<>();
        list.addAll(getDataSheetTypeList());
        list.addAll(getAllPossibleCombos("INFORMATION"));
        return list;
    }
}