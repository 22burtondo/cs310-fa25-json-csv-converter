package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.util.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            // Parse CSV
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> rows = reader.readAll();
            
            // Header
            String[] headers = rows.get(0);
            
            // JSON root object
            JsonObject root = new JsonObject();
            
            // Add column headings
            JsonArray colHeadings = new JsonArray();
            Collections.addAll(colHeadings, headers);
            root.put("ColHeadings", colHeadings);
            
            // ProdNums + Data
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();
            
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                
                // ProdNum (first column)
                prodNums.add(row[0]);
                
                // Data (remaining columns)
                JsonArray rowData = new JsonArray();
                
                // Title
                rowData.add(row[1]);
                
                // Season and Episode as integers
                rowData.add(Integer.parseInt(row[2]));
                rowData.add(Integer.parseInt(row[3]));
                
                // Remaining as strings
                rowData.add(row[4]);
                rowData.add(row[5]);
                rowData.add(row[6]);
                
                data.add(rowData);
            }
            
            root.put("ProdNums", prodNums);
            root.put("Data", data);
            
            result = root.toJson();

            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            JsonObject root = (JsonObject) Jsoner.deserialize(jsonString);
            
            // Get arrays
            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray prodNums = (JsonArray) root.get("ProdNums");
            JsonArray data = (JsonArray) root.get("Data");
            
            StringWriter sw = new StringWriter();
            CSVWriter writer = new CSVWriter(sw);
            
            // Write header
            writer.writeNext((String[]) colHeadings.toArray(new String[0]));
            
            // Write rows
            for (int i = 0; i < prodNums.size(); i++) {
                String prodNum = (String) prodNums.get(i);
                JsonArray rowData = (JsonArray) data.get(i);
                
                String[] row = new String[colHeadings.size()];
                row[0] = prodNum;
                
                for (int j = 0; j < rowData.size(); j++) {
                    Object val = rowData.get(j);

                    if (j == 2) { 
                        row[j + 1] = String.format("%02d", Integer.valueOf(val.toString()));
                    } 
                    else {
                        row[j + 1] = val.toString();
                    }
                }

                
                writer.writeNext(row);
            }
            
            writer.close();
            result = sw.toString();

            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
