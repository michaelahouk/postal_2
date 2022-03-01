package houk.postal.csv;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class App
{
    public static void main( String[] args ) {
        //Hardcoded values but could easily be found by searching the first line for column values
        final int IP_INDEX = 0;
        final int CODE_INDEX = 7;
        final int SIZE_INDEX = 8;

        final String zipSource = "log20170630.zip";
        final String zipDestination = "./resources";
        final String resultsPrefix = "./results/";

        try { //Unzip the file
            ZipFile zipFile = new ZipFile(zipSource);
            zipFile.extractAll(zipDestination);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("./resources/log20170630.csv")));
            String line = br.readLine();
            String[] lineSplit;

            //These will store the result values
            Set<String> ipAddresses = new HashSet<>();
            Map<String, Integer> codes = new HashMap<>();
            Double size = 0.0;

            while((line = br.readLine()) != null) {
                lineSplit = line.split(",");
                //Set will guarantee no duplicates are added
                ipAddresses.add(lineSplit[IP_INDEX]);

                //Add code to hashmap storing code and the count
                String code = lineSplit[CODE_INDEX];
                if (codes.containsKey(code)) {
                    codes.put(code, codes.get(lineSplit[CODE_INDEX])+1);
                } else if(isValidCode(code)) {
                    codes.put(code, 1);
                }

                //Increment size total if number is valid
                size = size + parseDouble(lineSplit[SIZE_INDEX]);
            }
            br.close();

            //Print IP addresses to file
            File outputFile = new File(resultsPrefix + "ip.txt");
            outputFile.getParentFile().mkdirs();
            FileWriter fileWriter = new FileWriter(outputFile);
            for (String ip : ipAddresses) {
                fileWriter.write(ip + '\n');
            }
            fileWriter.close();

            outputFile = new File(resultsPrefix + "codes.txt");
            fileWriter = new FileWriter(outputFile);

            //Print HTTP codes and their counts to file
            for (Map.Entry<String, Integer> entry : codes.entrySet()) {
                String code = entry.getKey();
                Integer count = entry.getValue();
                fileWriter.write(String.format("Code: %s Count: %d\n", code, count));
            }
            fileWriter.close();

            //Print size to file
            outputFile = new File(resultsPrefix + "size.txt");
            fileWriter = new FileWriter(outputFile);

            fileWriter.write(String.format("Total size: %s", size.toString()));
            fileWriter.close();

        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     *
     * @param str the string representing a double value
     * @return the parsed double
     */
    private static double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            //Size was invalid, will not count
            return 0;
        }
    }

    /**
     *
     * @param str string representing a possible HTTP status code
     * @return a boolean representing if the code was valid or not
     */
    private static boolean isValidCode(String str) {
        try {
            //Simple heuristic for determining if a status code is valid or not (could use list of valid values to be more accurate)
            double value = Double.parseDouble(str);
            return (value/100 < 6 && value > 0);
        } catch (Exception e) {
            //Code was invalid
            return false;
        }
    }

}
