package com.ezbob.ms.serviceshuffle.utils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShuffleHelper {

    public static List<Integer> shuffleArray(int n) {
        List<Integer> list = IntStream.rangeClosed(1, n).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        return list;
    }


}
