package game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 *
 * @author Burak Gök
 */
public class AssetManager {
    
    private final PApplet context;
    
    public AssetManager(PApplet context) {
        this.context = context;
    }
    
    public JSONObject readJSONObject(String fileName) {
        return context.loadJSONObject(fileName);
    }
    
    public JSONArray readJSONArray(String fileName) {
        return context.loadJSONArray(fileName);
    }
    
    private Object process(Object object) {
        if (object instanceof JSONObject)
            return process((JSONObject)object);
        else if (object instanceof JSONArray)
            return process((JSONArray)object);
        else if (object == JSONObject.NULL)
            return null;
        else
            return object; // String, Number, Boolean
    }
    
    public Map<String, Object> process(JSONObject object) {
        Map<String, Object> result = new HashMap<>(object.size());
        Iterator<String> iterator = object.keyIterator();
        
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = object.get(key);
            if (key.charAt(0) == '@') {
                key = key.substring(1);
                if (value instanceof String)
                    result.put(key, loadAsset((String)value));
                else if (value instanceof JSONArray) {
                    JSONArray jArray = (JSONArray) value;
                    PImage[] array = new PImage[jArray.size()];
                    for (int i = 0; i < jArray.size(); i++)
                        array[i] = loadAsset(jArray.getString(i));
                    result.put(key, array);
                }
            }
            else if (key.startsWith("int[")) {
                key = key.substring(4);
                result.put(key, ((JSONArray)value).getIntArray());
            }
            else result.put(key, process(value));
        }
        return result;
    }
    
    public Object[] process(JSONArray jArray) {
        Object[] array = new Object[jArray.size()];
        for (int i = 0; i < jArray.size(); i++)
            array[i] = process(jArray.get(i));
        return array;
    }

// <editor-fold defaultstate="collapsed" desc="biltrader json & getFiles()">
/*
    public Object readJSON(String fileName) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(
                    System.getProperty("user.dir"), "data", fileName));
            Node node = JSON.parse(new SmartBuffer(ByteBuffer.wrap(bytes)));
            return process(node);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(AssetManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private Object process(Node node) {
        if (node instanceof JsonObject)
            return process((JsonObject)node);
        else if (node instanceof JsonArray)
            return ((List<Node>)node.getValue()).stream()
                    .map((n) -> process(n)).toArray((s) -> new Object[s]);
        else
            return node.getValue();
    }
    
    private Map<String, Object> process(JsonObject object) {
        Map<String, Node> map = object.getValue();
        Map<String, Object> result = new HashMap<>(map.size());
        Iterator<String> iterator = map.keySet().iterator();
        
        while (iterator.hasNext()) {
            String key = iterator.next();
            Node node = object.get(key);
            if (key.charAt(0) == '@') {
                key = key.substring(1);
                if (node instanceof JsonString) {
                    PImage image = loadAsset((String) node.getValue());
                    result.put(key, image);
                }
                else if (node instanceof JsonArray) {
                    List<Node> list = (List<Node>) node.getValue();
                    PImage[] array = new PImage[list.size()];
                    for (int i = 0; i < list.size(); i++)
                        array[i] = loadAsset((String) list.get(i).getValue());
                    result.put(key, array);
                }
            }
            else result.put(key, process(node));
        }
        return result;
    }
    
    public static String[] getFiles(String path) {
        String[] paths = null;
        try {
            paths = Files
                .list(Paths.get(System.getProperty("user.dir"), "data", path))
                .map((p) -> {
                    int count = p.getNameCount();
                    return p.subpath(count - 2, count).toString();
                }).toArray((count) -> new String[count]);
        } catch (IOException ex) {
            Logger.getLogger(AssetManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return paths;
    }
*/
// </editor-fold>

    public PImage loadAsset(String name) {
//        int dot = name.lastIndexOf('.');
//        String imageName = name.substring(0, dot).replace('.', '/');
//        String extension = name.substring(dot + 1);
//        return context.loadImage(imageName, extension);
        return context.loadImage(name);
    }
    
    public static PImage mask(PImage orig, PImage mask) {
        if (orig.width < mask.width || orig.height < mask.height) {
            orig.loadPixels();
            // Ignore the alpha channel of the texture.
            for (int p = 0; p < orig.width * orig.height; p++)
                orig.pixels[p] &= 0xFFFFFF;
            
            PImage image = new PImage(mask.width, mask.height, PImage.ARGB);
            image.loadPixels();
            for (int y = 0, _y = 0; y < image.height; y++) {
                for (int x = 0, _x = 0; x < image.width; x++) {
                    int p = x + y * image.width;
                    int alpha = (mask.pixels[p] & PImage.ALPHA_MASK);
                    image.pixels[p] = alpha == 0 ? 0 : alpha
                            | orig.pixels[_x + _y * orig.width];
                    _x = (_x + 1) % orig.width;
                }
                _y = (_y + 1) % orig.height;
            }
            image.updatePixels();
            return image;
        }
        orig.mask(mask); // mask need to be gray-scale
        return orig;
    }
    
    public static PImage fill(PImage src, int srcColor, int destColor) {
        srcColor &= 0xFFFFFF;
        destColor &= 0xFFFFFF;
        src.loadPixels();
        for (int p = 0; p < src.width * src.height; p++)
            if ((src.pixels[p] & 0xFFFFFF) == srcColor)
                src.pixels[p] = (src.pixels[p] & PImage.ALPHA_MASK) | destColor;
        src.updatePixels();
        return src;
    }
}
