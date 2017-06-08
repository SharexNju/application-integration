package top.sharex;


import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Daniel on 2017/6/8.
 */
public class SmallSpider {

    public static void main(String[] args) {
        try {
            //加载document
            Document document = Jsoup.parse(new URL("http://istock.jrj.com.cn/list,600000,p1.html"), 10000);
            //使用css选择器选择元素  更多内容参见 http://www.w3school.com.cn/cssref/css_selectors.asp
            Elements elements = document.select("#topiclisttitle tbody tr");

            //新建需要保存的xml文档
            org.dom4j.Document xmlDocument = DocumentHelper.createDocument();
            //创建根节点
            org.dom4j.Element rootElement = xmlDocument.addElement("comments");

            //从1开始，0的时候为表头
            for (int i = 1; i < elements.size(); i++) {
                Element node = elements.get(i);
                Element hrefAndTitle = node.child(2).child(0);
                org.dom4j.Element commentElement = rootElement.addElement("comment");
                //选择属性
                String href = hrefAndTitle.attr("href");
                //选择文本
                String title = hrefAndTitle.text();
                String time = node.select("td:nth-child(5) span").text();

                commentElement.addElement("href").setText(href);
                commentElement.addElement("title").setText(title);
                commentElement.addElement("time").setText(time);
                System.out.printf("title:%s\thref:%s\ttime:%s\n", title, href, time);
            }

            //xml writer ，写入磁盘
            XMLWriter writer = new XMLWriter(new FileWriter("d:/comments.xml"));
            writer.write(xmlDocument);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
