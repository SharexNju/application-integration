package top.sharex;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Daniel on 2017/6/8.
 */
public class XSLTTransfer {
    /**
     *
     * @param source 需要转换的原xml文件
     * @param target 保存的目标文件位置
     * @param xslt 对应的xslt文件
     */
    public void trans(String source, String target, String xslt) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(getClass().getResourceAsStream(source));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(getClass
                    ().getResourceAsStream(xslt)));

            DocumentSource ds = new DocumentSource(document);

            DocumentResult dr = new DocumentResult();

            transformer.transform(ds, dr);

            XMLWriter xmlWriter = new XMLWriter(new FileWriter(new File(target)));
            xmlWriter.write(dr.getDocument());
            xmlWriter.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        XSLTTransfer xsltTransfer = new XSLTTransfer();

//        xsltTransfer.trans("/before_trans.xml", "D:/after_trans.xml", "/fei.xsl");
        xsltTransfer.trans("/000001.xml", "/Users/Jerry/Downloads/000001-trans.xml", "/rui.xsl");

    }
}
