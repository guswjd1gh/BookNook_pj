package com.hll.booknook;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URLEncoder;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class Item{
    public String Title = "";
    public String Link = "";
    public String ISBN = "";
}

class AladdinOpenAPIHandler extends DefaultHandler {
    public List<Item> Items;
    private Item currentItem;
    private boolean inItemElement = false;
    private String tempValue;

    public AladdinOpenAPIHandler( ){
        Items = new ArrayList<Item>();
    }

    public void startElement(String namespace, String localName, String qName, Attributes atts) {
        if (localName.equals("item")) {
            currentItem = new Item();
            inItemElement = true;
        } else if (localName.equals("title")) {
            tempValue = "";
        } else if (localName.equals("link")) {
            tempValue = "";
        }else if (localName.equals("ISBN")){
            tempValue = "";
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException{
        tempValue = tempValue + new String(ch,start,length);
    }

    public void endElement(String namespaceURI, String localName,String qName) {
        if(inItemElement){
            if (localName.equals("item")) {
                Items.add(currentItem);
                currentItem = null;
                inItemElement = false;
            } else if (localName.equals("title")) {
                currentItem.Title = tempValue;
            } else if (localName.equals("link")) {
                currentItem.Link = tempValue;
            }else if (localName.equals("isbn13")){
                currentItem.ISBN = tempValue;
            }
        }
    }

    public void parseXml(String xmlUrl) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        ParserAdapter pa = new ParserAdapter(sp.getParser());
        pa.setContentHandler(this);
        pa.parse(xmlUrl);
    }
}

public class AladdinOpenAPI {
    private static final String BASE_URL = "http://www.aladdin.co.kr/ttb/api/ItemSearch.aspx?";

    public static String GetUrl(String searchWord) throws Exception {
        Map<String,String> hm = new HashMap<String,String>();
        hm.put("ttbkey", "ttbsumin_han2252001");
        hm.put("Query", URLEncoder.encode(searchWord, "UTF-8"));
        hm.put("QueryType", "Keyword");
        hm.put("MaxResults", "100");
        hm.put("Sort","Accuracy");
        hm.put("start", "1");
        hm.put("SearchTarget", "All");
        hm.put("output", "xml");
        hm.put("Version", "20131101");

        StringBuffer sb = new StringBuffer();
        Iterator<String> iter = hm.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String val  = hm.get(key);
            sb.append(key).append("=").append(val).append("&");
        }

        return BASE_URL + sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String url = GetUrl("김영하");
        AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();
        api.parseXml(url);
        for(Item item : api.Items){
            System.out.println(item.Title + " : " + item.ISBN);
        }
    }
}