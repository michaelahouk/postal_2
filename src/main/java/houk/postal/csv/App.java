package houk.postal.csv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class App
{
    //Hardcoded values but could easily be found by searching the first line for column values
    final static int IP_INDEX = 0;
    final static int CODE_INDEX = 7;
    final static int SIZE_INDEX = 8;

    final static int BYTE_BUFFER = 4096;

    final static String ZIP_SOURCE = "output.zip";
    final static String ZIP_DEST = "./resources";
    final static String FILE_URL = "http://www.sec.gov/dera/data/Public-EDGAR-log-file-data/2017/Qtr2/log20170630.zip";
    final static String FILE_NAME = "log20170630.csv";

    public static void main (String[] args) {
        //Create the client
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(FILE_URL)
            .build();
        try {
            Response response = client.newCall(request).execute();

            //Read response as a bytestream
            BufferedInputStream input = new BufferedInputStream(response.body().byteStream());
            File file = new File(ZIP_SOURCE);
            OutputStream output = new FileOutputStream(file);

            byte[] dataBuffer = new byte[BYTE_BUFFER];
            int length = input.read(dataBuffer);

            //Iteratively read and write data to output file
            while (length != -1) {
                output.write(dataBuffer, 0, length);
                length = input.read(dataBuffer);
            }

            //Close input and output streams
            output.close();
            input.close();
        } catch (IOException e) {
            System.out.println("Encountered an error downloading the zip file");
        }

        //Unzip the file
        try {
            ZipFile zipFile = new ZipFile(ZIP_SOURCE);
            zipFile.extractAll(ZIP_DEST);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(
                new FileReader(new File(ZIP_DEST + "/" + FILE_NAME)));
            String line;
            String[] lineSplit;

            //These will store the result values
            Set<String> ipAddresses = new HashSet<>();
            Map<String, Integer> codes = new HashMap<>();
            Double size = 0.0;

            //Iterate through all lines in the file
            while((line = br.readLine()) != null) {
                lineSplit = line.split(",");
                //Add IP address to set (set will guarantee no duplicates)
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

            //Print size to file
            printSize(size);

            //Print HTTP codes and their counts to file
            printHTTPCodes(codes);

            //Print IP addresses to file
            printIPAddresses(ipAddresses);

        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     *
     * @param str the string representing a double value
     * @return the parsed double
     */
    protected static double parseDouble(String str) {
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
    protected static boolean isValidCode(String str) {
        try {
            //Simple heuristic for determining if a status code is valid or not (could use list of valid values to be more accurate)
            double value = Double.parseDouble(str);
            return (value/100 < 6 && value > 0);
        } catch (Exception e) {
            //Code was invalid
            return false;
        }
    }

    /**
     *
     * @param ipAddresses a set of IP Addresses to print
     * Results: prints output to console
     */
    private static void printIPAddresses(Set<String> ipAddresses) {
        System.out.println("Unique IP Addresses:");
        ipAddresses.forEach(ip -> {
            System.out.println(ip);
        });
    }

    /**
     *
     * @param codes a map of codes and their counts to print to a file
     * Results: prints output to file /prefix/codes.txt
     */
    private static void printHTTPCodes(Map<String, Integer> codes) {
        System.out.println("HTTP Codes and their counts:");
        codes.forEach((code, count) -> {
            System.out.println(String.format("Code: %s Count: %d", code, count));
        });
    }

    /**
     *
     * @param size a size to be printed
     * Results: prints output to console
     */
    private static void printSize(Double size) {
        System.out.println("Total size in bytes: " + size);
    }
}
