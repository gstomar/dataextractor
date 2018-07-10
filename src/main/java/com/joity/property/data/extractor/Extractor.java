package com.joity.property.data.extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.bind.SchemaOutputResolver;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class Extractor {

    private List<String> rentStats;
    private List<String> buyStats;

    private static String FOR_SALE ="for-sale";
    private static String TO_RENT ="to-rent";

    public Extractor() {
        rentStats = new ArrayList<>();
        buyStats = new ArrayList<>();
    }

    public void getPageLinks(String URL) {
        try {
            System.out.println("Url is " + URL);
            Document document = Jsoup.connect(URL).get();

            for (Element table : document.select("table")) {

                if(table.hasClass("stripe asking-price-table property-table")) {
                    Element houseTBodyData = table.select("tbody").first();
                    Element houseData = houseTBodyData.select("tr").first();
                    int size = houseData.children().size();
                    System.out.println("size " + size);
                    for(int start=2; start<size; start++) {
                        Element childData = houseData.child(start);
                        System.out.println(childData);
                        if(childData.toString().contains("strong")) {
//                            System.out.println(childData);
                            String priceString = childData.select("strong").first().toString();
                            String hrefString = childData.select("a").first().toString();
                            int beginIndex = priceString.indexOf(">");
                            int lastIndexOf = priceString.lastIndexOf("</");
                            String price = priceString.substring(beginIndex + 1, lastIndexOf);
                            System.out.println(price);
                            String[] hrefArray = hrefString.split("/");
                            System.out.println(hrefArray[1] + hrefArray[3]);
                            if (FOR_SALE.equals(hrefArray[1])) {
                                buyStats.add(new StringBuilder(hrefArray[3]).append("#").append(price).toString());
                            } else {
                                rentStats.add(new StringBuilder(hrefArray[3]).append("#").append(price).toString());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }



    public void writeToFile(String postCode, BufferedWriter writer) {
//        System.out.println(buyStats.size() + ":" + rentStats.size());
        if(buyStats.size() > 0 && rentStats.size() > 0) {
            StringBuilder rowData = new StringBuilder()
                .append(postCode);
            int buySize = buyStats.size();
            for (int start = 0; start < buySize; start++) {
                String buyPrice = buyStats.get(start);
                String[] buyPriceArray = buyPrice.split("#");
                rowData.append("#")
                    .append(buyPriceArray[0])
                    .append("#")
                    .append(buyPriceArray[1]);
            }
            int rentSize = rentStats.size();
            for (int start = 0; start < rentSize; start++) {
                String rentPrice = rentStats.get(start);
                String[] rentPriceArray = rentPrice.split("#");
                rowData.append("#")
                    .append(rentPriceArray[0])
                    .append("#")
                    .append(rentPriceArray[1]);
            }
            System.out.println(rowData);
            try {
                //save to file
                writer.write(rowData.toString());
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }

            buyStats.clear();
            rentStats.clear();
        }
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(LocalDate.now());
        Extractor bwc = new Extractor();
        try {
            BufferedReader fileReader = Files.newBufferedReader(Paths.get("src/main/resources/District_Postcodes.csv"));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("raw-buy-rent-stats-" + LocalDate.now() +".csv")));

            String postCode = fileReader.readLine();
            while (postCode != null) {
                bwc.getPageLinks("https://www.zoopla.co.uk/market/" + postCode);
                bwc.writeToFile(postCode, writer);
                postCode = fileReader.readLine();
            }
            writer.close();
            fileReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

        }
    }
}