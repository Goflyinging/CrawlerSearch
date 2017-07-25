package cn.lxing.dao;

import cn.lxing.entity.Article;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过Hbase数据库查询lucene查到文章的主键号 Created by lxing on 2017/3/12.
 */
@Component
public class ArticleDao {
    static Configuration conf = null;
    
    static {
        conf = HBaseConfiguration.create();
        conf.addResource("/hbase-site.xml");
    }
    
    public Article searchOnHbaseByRkey(String tableName, String rowkey) {
        Article article = null;
        Connection conn = null;
        HTable table = null;
        try {
            conn = ConnectionFactory.createConnection(conf);
            table = (HTable) conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowkey));
            Result result = table.get(get);
            article = new Article();
            article.setId(rowkey);
            byte[] value = result.getValue("author".getBytes(), "".getBytes());
            article.setAuthor(new String(value, "utf-8"));
            value = result.getValue("title".getBytes(), "".getBytes());
            article.setTitle(new String(value, "utf-8"));
            value = result.getValue("date".getBytes(), "".getBytes());
            article.setDate(new String(value, "utf-8"));
            value = result.getValue("content".getBytes(), "".getBytes());
            article.setContent(new String(value, "utf-8"));
            value = result.getValue("url".getBytes(), "".getBytes());
            article.setUrl(new String(value, "utf-8"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null != table) {
                try {
                    table.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return article;
    }

    /***
     * 通过rowkey集合来查找集合中的所有文章
     * @param tableName
     * @param rowkeyList
     * @return
     */
    public List<Article> searchByRkeyList(String tableName,
                                          List<String> rowkeyList) {
        Connection conn = null;
        HTable table = null;
        List<Article> articleList = new ArrayList<>();
        try {
            conn = ConnectionFactory.createConnection(conf);
            table = (HTable) conn.getTable(TableName.valueOf(tableName));
            List<Get> list = new ArrayList<Get>();
            for (String rowkey : rowkeyList) {
                System.out.println(rowkey);
                list.add(new Get(Bytes.toBytes(rowkey)));
            }
            Result[] results = table.get(list);
            for (Result result : results) {
                Article article = new Article();
                byte[] value = result.getValue("info".getBytes(),
                                               "id".getBytes());
                article.setId(Bytes.toString(value));
                value = result.getValue("info".getBytes(), "author".getBytes());
                article.setAuthor(Bytes.toString(value));
                value = result.getValue("info".getBytes(), "title".getBytes());
                article.setTitle(Bytes.toString(value));
                value = result.getValue("info".getBytes(), "date".getBytes());
                article.setDate(Bytes.toString(value));
                value =
                      result.getValue("info".getBytes(), "content".getBytes());
                article.setContent(Bytes.toString(value));
                value = result.getValue("info".getBytes(), "url".getBytes());
                article.setUrl(Bytes.toString(value));
                System.out.println(article);
                articleList.add(article);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null != table) {
                try {
                    table.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return articleList;
    }
}
