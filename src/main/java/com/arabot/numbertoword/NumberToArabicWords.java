package com.arabot.numbertoword;

import java.io.*;
import java.util.*;

/**
 * Created by Bayan on 19/07/2017.
 */
public class NumberToArabicWords {

    public static final String HOW_TO_USE = "اكتب الرقم بحروف اللغة العربية ليتم تحوليه الرقم الرياضي، للخروج اكتب كلمة خروج.. ";

    public static void main(String[] args) throws IOException {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

       File f= new File("map.txt");
       String pathMap = f.getAbsolutePath();
        map= readMapFile(pathMap);
        File fBig= new File("BigNumbersMap.txt");
        String pathBig = fBig.getAbsolutePath();

        LinkedHashMap<String, Integer> BigNumbersMap = new LinkedHashMap<>();
        BigNumbersMap= readMapFile(pathBig);

        System.out.println(HOW_TO_USE);
        Scanner scan = new Scanner(System.in);
        String sentence;
        while (!(sentence = scan.nextLine()).equals("خروج")) {

            System.out.println(convert(sentence, map, BigNumbersMap));
        }

    }

    public static LinkedHashMap<String, Integer> readMapFile(String fileName) throws IOException {
       Reader rd = new InputStreamReader(new FileInputStream(fileName), "utf-8");
       BufferedReader br = new BufferedReader(rd);
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        String fileChar;

        while ((fileChar = br.readLine()) != null) {
            String[] bindedChar = fileChar.split("=");
            if (bindedChar == null || (bindedChar.length != 2)) {
                continue;
            }
            map.put(bindedChar[0].trim(),
                    Integer.parseInt((bindedChar[1].trim())));
        }
        return map;
    }


    public static String generalize(String input) {
        input = input.replaceAll("[أآ]", "ا");
        input = input.replaceAll("[ةه]", "");
        input = input.replaceAll("[ئ]", "ي");
        return input.trim();
    }

    public static int processHundredsAndUnits(String input, LinkedHashMap<String, Integer> map) {
        String sub;
        if ((input.endsWith("ماي") && (sub = input.substring(0, input.indexOf("ماي"))) != null) || (input.endsWith("مي") && (sub = input.substring(0, input.indexOf("مي"))) != null))
            return Integer.valueOf(map.get(sub.trim()).toString()) * 100;
        if (((input.endsWith("ون") && (sub = input.substring(0, input.indexOf("ون"))) != null)) || (input.endsWith("ين") && (sub = input.substring(0, input.indexOf("ين"))) != null))
            return Integer.valueOf(map.get(sub.trim()).toString()) * 10;
        if ((input.endsWith("عشر") && (sub = input.substring(0, input.indexOf("عشر") - 1)) != null))
            return Integer.valueOf(map.get(sub.trim()).toString()) + 10;
        return 0;
    }


    public static int prepareSubPart(String s, LinkedHashMap map) {
        if (s.isEmpty())
            return 1;
        String sub;
        List<Integer> extractedNumbers = new ArrayList<>();
        String arrOfStrings[] = s.split("( و| و )");
        for (String e : arrOfStrings) {
            if (!(e.contains("مي") || e.contains("ست") || e.contains("تسع")))
                e = e.replaceAll("[ت]", "ث");
            e = e.replace("و ", "");
            if (map.get(e.trim()) != null) {
                sub = map.get(e.trim()).toString();
                extractedNumbers.add(Integer.valueOf(sub));
            } else
                extractedNumbers.add(processHundredsAndUnits(e, map));
        }
        return extractedNumbers.stream().mapToInt(Integer::intValue).sum();
    }

    public static int convert(String input, LinkedHashMap<String, Integer> map, HashMap<String, Integer> bigNumbersMap) {
        input = generalize(input);
        int result = 0;
        if (map.get(input.trim()) != null) {
            return map.get(input.trim());
        }
        for (Map.Entry<String, Integer> entry : bigNumbersMap.entrySet()) {
            if (input.contains(entry.getKey())) {
                String[] array = input.split(entry.getKey());
                result += entry.getValue() * prepareSubPart(array[0].trim(), map);
                if (array.length > 1) {
                    input = array[1];
                } else {
                    input = "";
                    break;
                }
            }
        }
        if (input != "") {
            result += prepareSubPart(input, map);
        }
        return result;
    }
}
