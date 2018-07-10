package com.joity.property.data.sort;

import com.joity.property.data.pojo.PropertyData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public final class HouseTypeDataSorter {

    private static String TWO_BED   =   "2-bedrooms";
    private static String THREE_BED =   "3-bedrooms";
    private static String FOUR_BED  =   "4-bedrooms";
    private static String FIVE_BED  =   "5-bedrooms";

    private static List<PropertyData> sorted2BedHouseData = new ArrayList<>();
    private static List<PropertyData> sorted3BedHouseData = new ArrayList<>();
    private static List<PropertyData> sorted4BedHouseData = new ArrayList<>();
    private static List<PropertyData> sorted5BedHouseData = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("raw-buy-rent-stats.csv"));

        try {
            Map<String, String> map = new LinkedHashMap<String, String>();
            String line = "";
            String processedDataLine = "";
            while ((line = reader.readLine()) != null) {
                String[] lineArray = line.split("#");
                String postCode = lineArray[0];
                Map<String, List<Long>> dataMap = new HashMap<>();
                for (int start = 1; start < lineArray.length; start++) {
                    String data = lineArray[start];
                    if (TWO_BED.equals(data) || THREE_BED.equals(data) || FOUR_BED.equals(data) || FIVE_BED.equals(data)) {
                        List<Long> defaultNumberData = new ArrayList<>();
                        defaultNumberData.add(0l);
                        defaultNumberData.add(0l);
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
                            dataMap.get(previousKey).set(0, priceInNumber);
                        } else {
                            dataMap.get(previousKey).set(1, priceInNumber);
                        }
                    }
                }
//                System.out.println("Full line " + postCode + ":" + dataMap);
                processHouseTypeData(postCode, dataMap);
            }
            //lets sort all the housing data now
            sortHousingData();

            //lets write the data in files
            writeHousingDataIntoFiles();

        }
        finally{
            reader.close();
        }
        System.out.println("The processing of Housing data has been finished.");
    }

    private static void writeHousingDataIntoFiles() {
        writeData(TWO_BED, sorted2BedHouseData);
        writeData(THREE_BED, sorted3BedHouseData);
        writeData(FOUR_BED, sorted4BedHouseData);
        writeData(FIVE_BED, sorted5BedHouseData);
    }

    private static void writeData(String housingType, List<PropertyData> houseData) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(housingType + "-sorted-buy-rent-stats.csv");
            for(PropertyData houseTypeData : houseData) {
                String processedDataLine = houseTypeData.getCsvString("#");
                writer.write(processedDataLine);
                writer.write('\n');
                writer.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch(Exception ex) {}
        }
    }

    private static void sortHousingData() {
        sorted2BedHouseData.sort(
            (PropertyData data1, PropertyData data2)
                ->  data1.getRentalReturn() < data2.getRentalReturn() ? 1
                    : data1.getRentalReturn() > data2.getRentalReturn() ? -1
                    : 0);

        sorted3BedHouseData.sort(
            (PropertyData data1, PropertyData data2)
                ->  data1.getRentalReturn() < data2.getRentalReturn() ? 1
                : data1.getRentalReturn() > data2.getRentalReturn() ? -1
                : 0);

        sorted4BedHouseData.sort(
            (PropertyData data1, PropertyData data2)
                ->  data1.getRentalReturn() < data2.getRentalReturn() ? 1
                : data1.getRentalReturn() > data2.getRentalReturn() ? -1
                : 0);

        sorted5BedHouseData.sort(
            (PropertyData data1, PropertyData data2)
                ->  data1.getRentalReturn() < data2.getRentalReturn() ? -1
                : data1.getRentalReturn() > data2.getRentalReturn() ? 1
                : 0);

    }

    private static void processHouseTypeData(String postCode, Map<String, List<Long>> dataMap) {
        StringBuilder dataLine = new StringBuilder(postCode);
        if(dataMap.containsKey(TWO_BED)) {
            List<Long> data = dataMap.get(TWO_BED);
            sorted2BedHouseData.add(PropertyData.PropertyDataBuilder.builder()
                .withPostCode(postCode)
                .withHouseType("2 Bed")
                .withHousePrice(data.get(0))
                .withRentalIncome(data.get(1).shortValue()).build());

        }
        if(dataMap.containsKey(THREE_BED)) {
            List<Long> data = dataMap.get(THREE_BED);
            sorted3BedHouseData.add(PropertyData.PropertyDataBuilder.builder()
                .withPostCode(postCode)
                .withHouseType("3 Bed")
                .withHousePrice(data.get(0))
                .withRentalIncome(data.get(1).shortValue()).build());

        }
        if(dataMap.containsKey(FOUR_BED)) {
            List<Long> data = dataMap.get(FOUR_BED);
            sorted4BedHouseData.add(PropertyData.PropertyDataBuilder.builder()
                .withPostCode(postCode)
                .withHouseType("4 Bed")
                .withHousePrice(data.get(0))
                .withRentalIncome(data.get(1).shortValue()).build());
//            System.out.println("4 Bed size : " + sorted4BedHouseData.size());
        }
        if(dataMap.containsKey(FIVE_BED)) {
            List<Long> data = dataMap.get(FIVE_BED);
            sorted5BedHouseData.add(PropertyData.PropertyDataBuilder.builder()
                .withPostCode(postCode)
                .withHouseType("5 Bed")
                .withHousePrice(data.get(0))
                .withRentalIncome(data.get(1).shortValue()).build());
//            System.out.println("5 Bed size : " + sorted5BedHouseData.size());
        }

    }
}
