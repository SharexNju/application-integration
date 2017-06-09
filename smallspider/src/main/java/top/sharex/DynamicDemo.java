package top.sharex;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Daniel on 2017/6/9.
 * 抓取动态网页
 * 使用phantomjs
 * 抓取后使用jsoup进行解析
 */
public class DynamicDemo {

    // 如果要更换运行环境，请注意exePath最后的phantom.exe需要更改。因为这个只能在window版本上运行。前面的路径名
    // 也需要和exePath里面的保持一致。否则无法调用
    private static String projectPath = DynamicDemo.class.getResource("/").toString().substring(6);
    private static String jsPath = projectPath + "phantomSpider.js";
    private static String exePath = projectPath + "phantomjs.exe";

    public static void main(String[] args) throws IOException {
        // 测试调用。传入url即可
        String html = getParsedHtml("http://guba.eastmoney.com/list,600000_41.html");

        Document document = Jsoup.parse(html);

        Element e = document.select("#articlelistnew div:last-child span span span").get(0);
        System.out.println(e.toString());
    }

    // 调用phantomjs程序，并传入js文件，并通过流拿回需要的数据。
    public static String getParsedHtml(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(exePath + " " + jsPath + " " + url);
        System.out.println(exePath + " " + jsPath + " " + url);
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sbf = new StringBuilder();
        String tmp = "";
        while ((tmp = br.readLine()) != null) {
            sbf.append(tmp);
        }
        return sbf.toString();
    }

}
