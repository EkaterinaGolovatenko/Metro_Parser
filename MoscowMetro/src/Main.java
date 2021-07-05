import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {


  public static void main(String[] args) {
    try {
      createJsonFile();
      JSONParser parser = new JSONParser();
      JSONObject jsonData = (JSONObject) parser.parse(new FileReader(
          "/Users/ekaterinagolovatenko/java_basics/MoscowMetro/resources/map.json"));
      JSONObject stationsObject = (JSONObject) jsonData.get("stations");
      stationsObject.keySet().forEach(lineNumberObject ->
      {
        String lineNumber = (String) lineNumberObject;
        JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
        System.out.println("Линия " + lineNumber + ", кличество станций: " + stationsArray.size());
      });
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void createJsonFile() {
    try {
      Document doc = Jsoup.connect("https://www.moscowmap.ru/metro.html#lines").maxBodySize(0)
          .get();
      Elements elements = doc
          .select("span.js-metro-line.t-metrostation-list-header.t-icon-metroln");
      HashMap<String, String> linesAndNumbers = new HashMap<>();
      for (Element element : elements) {
        String number = element.attr("data-line");
        String line = element.text();
        linesAndNumbers.put(number, line);
      }
      Elements stations = doc.select("div.js-metro-stations");
      HashMap<String, ArrayList<String>> stationsAndNumbers = new HashMap<>();
      for (Element station : stations) {
        String num = station.attr("data-line");
        String stat = station.text();
        String statPure = stat.replaceAll("[0-9.]", "");
        String statPureTrim = statPure.trim();
        String statPureFinal = statPureTrim.replaceAll("  ", ",");
        ArrayList<String> myList = new ArrayList<>(Arrays.asList(statPureFinal.split(",")));
        stationsAndNumbers.put(num, myList);
      }

      JSONArray jLines = new JSONArray();
      for (Map.Entry<String, String> entry : linesAndNumbers.entrySet()) {
        JSONObject object = new JSONObject();
        object.put("number", entry.getKey());
        object.put("name", entry.getValue());
        jLines.add(object);
      }
      JSONObject jsonStations = new JSONObject();
      jsonStations.putAll(stationsAndNumbers);
      JSONObject mainObject = new JSONObject();
      mainObject.put("lines", jLines);
      mainObject.put("stations", jsonStations);
      FileWriter file = new FileWriter(
          "/Users/ekaterinagolovatenko/java_basics/MoscowMetro/resources/map.json");
      file.write(mainObject.toJSONString());
      file.flush();
      file.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}

