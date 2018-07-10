package com.joity.property.data.sort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public final class PlainDataSorter {

    private static String TWO_BED   =   "2-bedrooms";
    private static String THREE_BED =   "3-bedrooms";
    private static String FOUR_BED  =   "4-bedrooms";
    private static String FIVE_BED  =   "5-bedrooms";

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("raw-buy-rent-stats.csv"));
        FileWriter writer = new FileWriter("sorted-buy-rent-stats.csv");
        try {
            Map<String, String> map = new LinkedHashMap<String, String>();
            String line = "";
            String processedDataLine = "";
            while ((line = reader.readLine()) != null) {
                String[] lineArray = line.split("#");
                String postCode = lineArray[0];
                Map<String, List<Number>> dataMap = new HashMap<>();
                for (int start = 1; start < lineArray.length; start++) {
                    String data = lineArray[start];
                    if (TWO_BED.equals(data) || THREE_BED.equals(data) || FOUR_BED.equals(data) || FIVE_BED.equals(data)) {
                        List<Number> defaultNumberData = new ArrayList<>();
                        defaultNumberData.add(0);
                        defaultNumberData.add(0);
                        dataMap.putIfAbsent(data, defaultNumberData);
                    } else {
                        String previousKey = lineArray[start - 1];
                        String price = lineArray[start];
                        boolean isRentalAmount = false;
                        if (price.contains(" pcm")) {
                            price = price.replaceAll(" pcm", "");
                            isRentalAmount = true;
                        }
                        Long priceInNumber = Long.parseLong(price.substring(1).replaceAll(",", ""));
                        if (!isRentalAmount) {
                            dataMap.get(previousKey).add(0, priceInNumber);
                        } else {
                            dataMap.get(previousKey).add(1, priceInNumber);
                        }
                    }
                }
                processedDataLine = makeProcessedDataLine(postCode, dataMap);
                writer.write(processedDataLine);
                writer.write('\n');
                writer.flush();
            }
        }
        finally{
            reader.close();
            writer.close();
        }
    }

    private static String makeProcessedDataLine(String postCode, Map<String, List<Number>> dataMap) {
        StringBuilder dataLine = new StringBuilder(postCode);
        if(dataMap.containsKey(TWO_BED)) {
            List<Number> data = dataMap.get(TWO_BED);
            dataLine.append("#")
                .append("2 Bed");
            for(int start=0; start<2; start++) {
                dataLine.append("#")
                    .append(data.get(start));
            }

        }
        else{
            dataLine.append("#")
                .append("2 Bed")
            .append("0#0");
        }

        if(dataMap.containsKey(THREE_BED)) {
            List<Number> data = dataMap.get(THREE_BED);
            dataLine.append("#")
                .append("3 Bed");
            for(int start=0; start<2; start++) {
                dataLine.append("#")
                    .append(data.get(start));
            }

        }
        else{
            dataLine.append("#")
                .append("3 Bed")
                .append("0#0");
        }

        if(dataMap.containsKey(FOUR_BED)) {
            List<Number> data = dataMap.get(FOUR_BED);
            dataLine.append("#")
                .append("4 Bed");
            for(int start=0; start<2; start++) {
                dataLine.append("#")
                    .append(data.get(start));
            }

        }
        else{
            dataLine.append("#")
                .append("4 Bed")
                .append("0#0");
        }

        if(dataMap.containsKey(FIVE_BED)) {
            List<Number> data = dataMap.get(FIVE_BED);
            dataLine.append("#")
                .append("5 Bed");
            for(int start=0; start<2; start++) {
                dataLine.append("#")
                    .append(data.get(start));
            }

        }
        else{
            dataLine.append("#")
                .append("5 Bed")
                .append("0#0");
        }
        System.out.println("processed line : " + dataLine);
        return dataLine.toString();
    }

}
