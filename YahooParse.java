package trunk.Control;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import trunk.Model.Equity;
import trunk.Model.Portfolio;

import javax.sound.sampled.Port;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class YahooParse {
    public ArrayList<Double> parseXML(String xmlRecords, Portfolio portfolio) throws Exception{

        ArrayList<Double> askingPrices = new ArrayList<Double>();
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("quote");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            NodeList askPrice = element.getElementsByTagName("Ask");
            Element line1 = (Element) askPrice.item(0);

            NodeList tickerSym = element.getElementsByTagName("Symbol");
            Element line2 = (Element) tickerSym.item(0);

            String sym = getCharacterDataFromElement(line2);

            if (getCharacterDataFromElement(line1).isEmpty()){
                List<Equity> match = portfolio.equityList2.stream().filter(e -> e.tickerSymbol.equals(sym)).collect(Collectors.toList());
                askingPrices.add(match.get(0).acquisitionPrice);
            } else{
                askingPrices.add(Double.parseDouble(getCharacterDataFromElement(line1)));
            }

        }

        return askingPrices;
    }

    public String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }
}
