package top.sharex;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;

/**
 * Created by Daniel on 2017/6/9.
 */
public class FeifeiSpider implements SpiderInterface {
    String urlPartern = "http://istock.jrj.com.cn/list,%s,p%d.html";
    int currentYear;
    int currentMonth;
    int currentSize;
    final int maxSize = 200;
    final int minSize = 50;

    @Override
    public void getData(String code, String outputFile) throws SpiderException {
        int currentPage = 1;
        currentYear = 2017;
        currentMonth = -1;
        currentSize = 0;
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("all");

        rootElement.addElement("stockCode").setText(code);
        FileWriter outPutFileWriter = null;
        try {
            outPutFileWriter = new FileWriter(new File(outputFile));
        } catch (IOException e) {
            System.out.printf("file:%s open error\n", outputFile);
            e.printStackTrace();
        }

        while (true) {
            System.out.printf("\tstart page:%d\n", currentPage);
            int flag = loadSinglePage(rootElement, String.format(urlPartern, code, currentPage));
            if (flag == -1)
                break;
            currentPage++;
        }

        XMLWriter writer = null;
        try {
            writer = new XMLWriter(outPutFileWriter);
            writer.write(document);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 返回-1则代表所有页面加载完成
     *
     * @param rootElement
     * @param url
     * @return
     */
    private int loadSinglePage(Element rootElement, String url) throws SpiderException {
        try {
            org.jsoup.nodes.Document document = null;
            int times = 1;
            URL connectURL = new URL(url);
            while (true) {
                try {
                    if (times >= 5) {
                        throw new SpiderException(String.format("url:%s connecting error after " +
                                "reconnecting 5 times\n", url));
                    }
                    document = Jsoup.parse(connectURL, 15000);
                    break;
                } catch (SocketException e) {
                    System.out.printf("connect error,reconnect times:%d\n", times);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    times++;
                }
            }
            Elements elements = document.select("#topiclisttitle tbody tr");
            if (elements.size() == 0)
                return -1;

            for (int i = 1; i < elements.size(); i++) {
                org.jsoup.nodes.Element e = elements.get(i);
                String date = e.select("td:nth-child(5) span").text();
                String[] dateFields = date.split("-");
                boolean matchs = date.matches("\\d{2}-\\d{2}");
                if (!matchs)
                    continue;
                int month = Integer.valueOf(dateFields[0]);
                if (currentMonth == -1 || currentMonth >= month) {
                    currentMonth = month;
                } else {
                    currentYear--;
                    currentMonth = month;
                }

                date = currentYear + "-" + date;

                if (date.compareTo("2015-06-01") < 0 && currentSize > minSize)
                    return -1;

                String click = e.select("td:nth-child(1) span").text();
                String reply = e.select("td:nth-child(2) span").text();
                String title = e.select("td:nth-child(3) a").text();
                String comment_url = e.select("td:nth-child(3) a").attr("href");
                String author = e.select("td:nth-child(4) a").text();
                String author_url = e.select("td:nth-child(4) a").attr("href");

                Element subElement = rootElement.addElement("single");
                subElement.addElement("title").setText(title);
                subElement.addElement("url").setText(comment_url);
                subElement.addElement("date").setText(date);
                subElement.addElement("author").setText(author);
                subElement.addElement("author_url").setText(author_url);
                subElement.addElement("click").setText(click);
                subElement.addElement("reply").setText(reply);

                currentSize++;
                if (currentSize >= maxSize)
                    return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new SpiderException(String.format("url:%s not found", url));
        }
        return 0;
    }

    public static void main(String[] args) {
        FeifeiSpider feifeiSpider = new FeifeiSpider();
        String filePath = FeifeiSpider.class.getResource("/").getPath() + "data/feifei/600000.xml";
        try {
            feifeiSpider.getData("600000", filePath);
        } catch (SpiderException e) {
            e.printStackTrace();
        }
    }
}
