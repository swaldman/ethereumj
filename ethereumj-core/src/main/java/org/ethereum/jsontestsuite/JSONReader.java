package org.ethereum.jsontestsuite;

import org.ethereum.config.SystemProperties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class JSONReader {

    public static String loadJSON(String filename) {
        String json = "";
        if (!SystemProperties.CONFIG.vmTestLoadLocal())
            json = getFromUrl("https://raw.githubusercontent.com/ethereum/tests/develop/" + filename);
        return json.isEmpty() ? getFromLocal(filename) : json;
    }

    public static String loadJSONFromCommit(String filename, String shacommit) {
        String json = "";
        if (!SystemProperties.CONFIG.vmTestLoadLocal())
            json = getFromUrl("https://raw.githubusercontent.com/ethereum/tests/" + shacommit + "/" + filename);
        return json.isEmpty() ? getFromLocal(filename) : json;
    }

    public static String getFromLocal(String filename) {
        System.out.println("Loading local file: " + filename);
        try {
            if (System.getProperty("ETHEREUM_TEST_PATH") == null) {
                System.out.println("ETHEREUM_TEST_PATH is not passed as a VM argument, please make sure you pass it " +
                        "with the correct path");
                return "";
            }
            System.out.println("From: " + System.getProperty("ETHEREUM_TEST_PATH"));
            File vmTestFile = new File(System.getProperty("ETHEREUM_TEST_PATH") + filename);
            return new String(Files.readAllBytes(vmTestFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFromUrl(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.connect();
            InputStream in = conn.getInputStream();
            rd = new BufferedReader(new InputStreamReader(in));
            System.out.println("Loading remote file: " + urlToRead);
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getTestBlobForTreeSha(String shacommit, String testcase){

        String result = getFromUrl("https://api.github.com/repos/ethereum/tests/git/trees/" + shacommit);

        JSONParser parser = new JSONParser();
        JSONObject testSuiteObj = null;

        List<String> fileNames = new ArrayList<String>();
        try {
            testSuiteObj = (JSONObject) parser.parse(result);
            JSONArray tree = (JSONArray)testSuiteObj.get("tree");

            for (Object oEntry : tree) {
                JSONObject entry = (JSONObject) oEntry;
                String testName = (String) entry.get("path");
                if ( testName.equals(testcase) ) {
                    String blobresult = getFromUrl( (String) entry.get("url") );

                    testSuiteObj = (JSONObject) parser.parse(blobresult);
                    String blob  = (String) testSuiteObj.get("content");
                    byte[] valueDecoded= Base64.decodeBase64(blob.getBytes() );
                    //System.out.println("Decoded value is " + new String(valueDecoded));
                    return new String(valueDecoded);
                }
            }
        } catch (ParseException e) {e.printStackTrace();}

        return "";
    }

    public static List<String> getFileNamesForTreeSha(String sha){

        String result = getFromUrl("https://api.github.com/repos/ethereum/tests/git/trees/" + sha);

        JSONParser parser = new JSONParser();
        JSONObject testSuiteObj = null;

        List<String> fileNames = new ArrayList<String>();
        try {
            testSuiteObj = (JSONObject) parser.parse(result);
            JSONArray tree = (JSONArray)testSuiteObj.get("tree");

            for (Object oEntry : tree) {
                JSONObject entry = (JSONObject) oEntry;
                String testName = (String) entry.get("path");
                fileNames.add(testName);
            }
        } catch (ParseException e) {e.printStackTrace();}

        return fileNames;
    }
}
