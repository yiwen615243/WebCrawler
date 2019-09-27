import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHandler {
    private String jsonPath;

    final private static String defaultPath = "C:\\Users\\Yiwen Gu\\IdeaProjects\\WebCrawler\\src\\main\\resources\\test.json";

    protected List<String> keyList = new ArrayList<String>();

    protected List<String> URLList = new ArrayList<String>();

    public JsonHandler() {
        this(defaultPath);
    }

    public JsonHandler(String path) {
        this.jsonPath = path;
    }

    private void iniDataList(JsonNode node){
        Iterator<JsonNode> elements = node.elements();
        while(elements.hasNext()){
            JsonNode element = elements.next();
            keyList.add(element.asText());
        }
    }

    private JsonNode getJsonNodeByKey(String key, JsonNode rootNode){
        return rootNode.path(key);
    }

    public void printAll(){
        System.out.println(keyList);
        System.out.println(URLList);
    }

    public void convertToURLList(List<String> keyList){
        Iterator<String> iter = keyList.iterator();
        while(iter.hasNext()){
           this.URLList.add(String.format("https://www.amazon.com/dp/%s", iter.next()));
        }
    }

    public void readProcess(String key) {
        try{
            byte[] jsonData = Files.readAllBytes(Paths.get(jsonPath));

            ObjectMapper objMapper = new ObjectMapper();

            JsonNode rootNode = objMapper.readTree(jsonData);
            JsonNode keyNode = getJsonNodeByKey(key, rootNode);

            iniDataList(keyNode);
            convertToURLList(keyList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}