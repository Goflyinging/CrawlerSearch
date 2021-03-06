import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by lxing on 2017/4/11.
 */
public class HbaseTest {

    public static Connection conn = null;
    public static TableName saveTable = TableName.valueOf("savetest");
    public static TableName urlTable = TableName.valueOf("crawlertest");
    public static TableName articleTable = TableName.valueOf("articletest");
    Configuration conf = HBaseConfiguration.create();

    @Before
    public void setup() throws IOException {
        conf.addResource("hbase-site.xml");
        conn = ConnectionFactory.createConnection(conf);
    }

    @After
    public void after() throws IOException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void find() throws IOException {
        Table table = conn.getTable(articleTable);
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);
        Iterator<Result> it = scanner.iterator();
        int i = 0;
        while (it.hasNext()) {
            System.out.println(i++);
            Result next = it.next();
            for (Cell cell : next.rawCells()) {// 遍历每一行的各列
                System.out.println(
                        "Rowkey :" + Bytes.toString(CellUtil.cloneRow(cell)) +
                                "   Familiy:Quilifier : " + Bytes.toString(CellUtil.cloneQualifier(cell)) +
                                "   Value : " + Bytes.toString(CellUtil.cloneValue(cell))

                );
            }
        }

    }

    @Test
    public void search() throws IOException {
        Table table = conn.getTable(articleTable);
        Get get = new Get("70161887_csdn".getBytes());
//        get.addColumn("cf1".getBytes(),"name".getBytes());
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {// 遍历每一行的各列
            System.out.println(
                    "Rowkey : " + Bytes.toString(CellUtil.cloneRow(cell)) +
                            "   Familiy:Quilifier : " + Bytes.toString(CellUtil.cloneQualifier(cell)) +
                            "   Value : " + Bytes.toString(CellUtil.cloneValue(cell))

            );
        }
    }

//    @Test
//    public void testClearCache() throws Exception {
//
//        HbaseUtil.clearCache(conf);
//    }
}


