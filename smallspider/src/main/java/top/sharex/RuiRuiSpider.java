package top.sharex;

import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Daniel on 2017/6/9.
 */
public class RuiRuiSpider implements SpiderInterface {
    @Override
    public void getData(String code, String outputFile)  throws SpiderException{
        try {
            //加载document
            URL url = new URL("https://t.10jqka.com.cn/guba/"+code);
            Document document = Jsoup.parse(url, 10000);

            //新建需要保存的xml文档
            org.dom4j.Document xmlDocument = DocumentHelper.createDocument();
            //创建根节点
            org.dom4j.Element rootElement = xmlDocument.addElement("stockinfo");

            //获取股票信息
            String name = document.select(".stock-name").text();
            String stockcode = document.select(".stock-code").text();
            rootElement.addElement("stock-name").setText(name);
            rootElement.addElement("stock-code").setText(stockcode);
            System.out.printf("name:%s\tcode:%s\n",name,stockcode);

            //获取页数
            String pagenum = document.select(".sumNum").text();

            //获取fid
            String fid = document.select("#globalParam").attr("data-fid");
            System.out.println("fid:"+fid);

            try {
                int sumnum = Integer.parseInt(pagenum);
                System.out.println("页数：" + sumnum);
                //遍历每一页的post
                for (int i = 1; i <= sumnum; i++) {
                    RuiRuiSpider ruiRuiSpider = new RuiRuiSpider();
                    ruiRuiSpider.onePage(xmlDocument,i,fid);
                }

            } catch (NumberFormatException e){
                e.printStackTrace();
            }


            //xml writer ，写入磁盘
            XMLWriter writer = new XMLWriter(new FileWriter(outputFile));
            writer.write(xmlDocument);
            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onePage(org.dom4j.Document xmlDocument,int pageNum,String fid) {
        try {
            //获得ul
            Connection conn = Jsoup.connect("https://t.10jqka.com.cn/newcircle/post/getPostList/");
            conn.data("fid", fid);
            conn.data("type", "0");
            conn.data("page", Integer.toString(pageNum));
            Connection.Response response = conn.ignoreContentType(true).execute();
            String body = response.body();
            JSONObject jsonObject = null;
            //取json中的html
            try {
                jsonObject = new JSONObject(body);
                System.out.println(jsonObject);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
            JSONObject result = null;
            try {
                result = (JSONObject) jsonObject.get("result");
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
            Document document = null;
            try {
                document = Jsoup.parse(result.get("html").toString());
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }

            //取ul
            Elements elements = document.select(".postlist-ul .post-single");
            //取每一个li的内容
            for (int i = 0; i < elements.size(); i++) {
                Element node = elements.get(i);
                Element a = node.child(1);
                org.dom4j.Element onepostElement = xmlDocument.getRootElement().addElement("one-post");
                //选择属性
                String href = a.attr("href");
                //选择文本
                String title = a.child(0).text();
                String content = a.child(1).text();
                String time = node.child(2).child(1).text();

                onepostElement.addElement("href").setText(href);
                onepostElement.addElement("title").setText(title);
                onepostElement.addElement("content").setText(content);
                onepostElement.addElement("time").setText(time);
                System.out.printf("%d title:%s\tcontent:%s\thref:%s\ttime:%s\n", i + 1, title, content, href, time);
            }


        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void main(String[] args) {
        RuiRuiSpider feifeiSpider = new RuiRuiSpider();
        String filePath = RuiRuiSpider.class.getResource("/").getPath() + "data/rui/600000.xml";
        try {
            feifeiSpider.getData("600000", filePath);
        } catch (SpiderException e) {
            e.printStackTrace();
        }
    }

}


