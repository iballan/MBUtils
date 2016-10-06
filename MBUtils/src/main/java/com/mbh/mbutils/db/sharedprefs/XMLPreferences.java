package com.mbh.mbutils.db.sharedprefs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created By MBH on 2016-06-21.
 */
public class XMLPreferences implements IPreferences {
    private final String path;

    public XMLPreferences(String path) {
        this.path = path;
    }

    public boolean contains(String key) {
        return getContentByKey(key) == null;
    }

    public int getInt(String key, int defValue) {
        if( getContentByKey(key) == null)
            return defValue;
        return Integer.valueOf(key);
    }

    public String getString(String key, String defValue) {
        if( getContentByKey(key) == null)
            return defValue;
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        String value = getContentByKey(key);
        return value==null?defValue:value.equals("t");
    }

    public void putInt(String key, int value) {
        putContentByKey(key, value+"");
    }

    @Override
    public void put(String key, int value) {
        putInt(key, value);
    }

    public void putString(String key, String value) {
        putContentByKey(key, value);
    }

    @Override
    public void put(String key, String value) {
        putString(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        putContentByKey(key, value?"t":"f");
    }

    @Override
    public void put(String key, boolean value) {
        putBoolean(key, value);
    }

    private String getContentByKey(String key) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = builder.parse(fileInputStream);
            Element root = dom.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(key);
            if (nodes.getLength() > 0)
                return nodes.item(0).getTextContent();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void putContentByKey(String key, String content) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dom = builder.parse(fileInputStream);
            Element root = dom.getDocumentElement();
            NodeList nodes = root.getElementsByTagName(key);
            if (nodes.getLength() > 0)
                nodes.item(0).setTextContent(content);
            else {
                Element newElement = dom.createElement(key);
                newElement.setTextContent(content);
                root.appendChild(newElement);
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(new File(path));
            Source input = new DOMSource(dom);
            transformer.transform(input, output);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

//    public interface IPreferences {
//        boolean contains(String key);
//        int getInt(String key, int defValue);
//        String getString(String key, String defValue);
//        void putInt(String key, int value);
//        void putString(String key, String value);
//    }
}
