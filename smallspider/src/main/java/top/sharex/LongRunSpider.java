package top.sharex;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Daniel on 2017/6/9.
 */
public class LongRunSpider {
    private static final String FEIFEI_DIR = "datadir.feifei";
    private static final String RUIRUI_DIR = "datadir.ruirui";
    private static final String TAOZI_DIR = "datadir.taozi";
    private static final String FAILED_FILE = "failedfile";

    /**
     * 错误信息输出的格式，依次为时间、代码、错误信息
     */
    private static final String FIAL_OUTPUT_PATTERN = "%s\t%s\t%s\n";

    List<CodeName> allCodes;

    BufferedWriter failedFileWriter;

    Properties configProperties;

    public LongRunSpider() {
        configProperties = new Properties();
        try {
            configProperties.load(getClass().getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        initCodes();

        try {
            failedFileWriter = new BufferedWriter(new FileWriter(configProperties.getProperty
                    (FAILED_FILE), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void initCodes() {
        allCodes = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream("/codeName.txt")));
        String tem;
        try {
            while ((tem = reader.readLine()) != null) {
                String[] fields = tem.split(" ");
                allCodes.add(new CodeName(fields[0], fields[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将执行错误的代码保存到单独的文件中
     *
     * @param code
     * @param message
     */
    void writeWrong(String code, String message) {
        String outPutInfo = String.format(FIAL_OUTPUT_PATTERN, new Date(System.currentTimeMillis()).toString(),
                code, message);
        try {
            failedFileWriter.write(outPutInfo);
            failedFileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void execute(SpiderInterface spiderInterface, String whichPerson) {
        String outputDIR = configProperties.getProperty(whichPerson);
        int total = allCodes.size();
        for (CodeName codeName : allCodes) {
            System.out.printf("start get %s \t %d left\n", codeName.code, total);
            try {
                spiderInterface.getData(codeName.code, outputDIR + File.separator
                        + codeName.code + ".xml");
            } catch (SpiderException e) {
                e.printStackTrace();
                writeWrong(codeName.code, e.getMessage());
            }
            total--;
        }
    }

    void executeFei() {
        execute(new FeifeiSpider(), FEIFEI_DIR);
    }

    void executeTaozi() {
        execute(new TaoziSpider(), TAOZI_DIR);
    }

    void executeRuirui() {
        execute(new RuiRuiSpider(), RUIRUI_DIR);
    }

    public static void main(String[] args) {
        LongRunSpider longRunSpider = new LongRunSpider();
        longRunSpider.executeFei();
    }

}

class CodeName {
    String code;
    String name;

    public CodeName(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

