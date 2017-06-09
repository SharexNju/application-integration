package top.sharex;

/**
 * Created by Daniel on 2017/6/9.
 */
public interface SpiderInterface {
    /**
     * 需要实现此接口，将对应股票代码的comments信息以xml文件的格式保存到指定的文件当中
     * @param code 股票代码
     * @param outputFile 文件路径
     */
    void getData(String code,String outputFile) throws SpiderException;
}
