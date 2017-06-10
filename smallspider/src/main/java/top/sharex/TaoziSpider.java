package top.sharex;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.SocketException;
import java.net.URL;

/**
 * Created by Daniel on 2017/6/9.
 */
public class TaoziSpider implements SpiderInterface {
    String urlPattern = "http://guba.eastmoney.com/list,%s,f_%d.html";
    int currentYear;
    int currentMonth;
    int currentSize;
    final int maxSize = 1000;
    final int minSize = 50;

    @Override
    public void getData(String code, String outputFile)  throws SpiderException{

        int currentPage = 1;
        currentYear = 2017;
        currentMonth = -1;
        currentSize = 0;
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("all");

        rootElement.addElement("stockCode").setText(code);
        FileWriter outPutFileWriter = null;
        try {
            outPutFileWriter = new FileWriter(outputFile);
        } catch (IOException e) {
            System.out.printf("file:%s open error\n", outputFile);
            e.printStackTrace();
        }

        while (true) {
            System.out.printf("\tstart page:%d\n", currentPage);
            int flag = spideSinglePage(rootElement, String.format(urlPattern, code, currentPage));
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
                    System.out.println("closed!");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private int spideSinglePage(Element rootElement, String url) throws SpiderException {

        System.out.println(url);

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
                    document = Jsoup.parse(connectURL, 10000);
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
            Elements elements = document.select("#articlelistnew div");
            if (elements.size() == 0)
                return -1;

            for (int i = 1; i < elements.size() - 1; i++) {
                org.jsoup.nodes.Element e = elements.get(i);
                String date = e.child(4).text();

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
                System.out.println(date);

                if (date.compareTo("2015-01-01") < 0 && currentSize > minSize)
                    return -1;

                int subElementNum = e.child(2).childNodeSize();
//            System.out.println(subElement);
                org.jsoup.nodes.Element hrefAndTitle = null;
                if (subElementNum == 3) {
                    hrefAndTitle = e.child(2).child(1);
                } else if (subElementNum == 2) {
                    hrefAndTitle = e.child(2).child(1);
                } else if (subElementNum == 1) {
                    hrefAndTitle = e.child(2).child(0);
                }

                String read = e.child(0).text();
                String comment = e.child(1).text();
                String title = hrefAndTitle.text();
                String comment_url = "http://guba.eastmoney.com/" + hrefAndTitle.attr("href");
                String author = e.child(3).child(0).text();
                String author_url = e.child(3).child(0).attr("href");

                Element subElement = rootElement.addElement("single");
                subElement.addElement("title").setText(title);
                subElement.addElement("url").setText(comment_url);
                subElement.addElement("date").setText(date);
                subElement.addElement("author").setText(author);
                subElement.addElement("author_url").setText(author_url);
                subElement.addElement("click").setText(read);
                subElement.addElement("reply").setText(comment);

                System.out.printf("read:%s\tcomment:%s\ttitle:%s\tcomment_url:%s\tdate:%s\tauthor:%s\tauthor_url:%s\n",
                        read, comment, title, comment_url, date, author, author_url);


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

        TaoziSpider taoZiSpider = new TaoziSpider();
        String code = null;
        try (BufferedReader br =
                     new BufferedReader(new FileReader("/Users/taozihan/yyjc/application-integration/smallspider/target/classes/codeName.txt"))) {
//            while(br.readLine()!=null){
//                String line = br.readLine();
//
////                System.out.println(line);
//                code = line.substring(0, 6);
////                System.out.println(code);

            code = "600000";
                String filePath = TaoziSpider.class.getResource("/").getPath() + "data/taotao/"+code+".xml";
                try {

                    taoZiSpider.getData(code, filePath);
                } catch (SpiderException e) {
                    e.printStackTrace();
                }

//            }
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }





    }
}
