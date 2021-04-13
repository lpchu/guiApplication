package com.company;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


/**
 * Scratchpad for testing
 */
public class HelloWorld {

    public void swap(int[] intArray, int idx1, int idx2) {
        /*
        Swaps values between idx1 and idx2
         */
        int temp = intArray[idx1];
        intArray[idx1] = intArray[idx2];
        intArray[idx2] = temp;
    }

    public void selectionSort(int[] vals) {
        int indexMin;
        for (int i=0; i < vals.length-1; i++) {
            indexMin = i;
            for (int j=i+1; j < vals.length; j++) {
                if (vals[j] < vals[indexMin]) {
                    indexMin = j;
                }
            }
            swap(vals, indexMin, i);
        }
    }

    public static void main(String[] args) {
        HashMap<String, ArrayList<Integer>> map = new HashMap<>();
        ArrayList<Integer> array = new ArrayList<>(Arrays.asList(1, 2, 3));
        map.put("test", array);
        System.out.println(map);
        System.out.println(map.get("test"));
        System.out.println(map.get("test").get(0));
        map.get("test").set(0,10);
        System.out.println(map.get("test"));
    }
}
