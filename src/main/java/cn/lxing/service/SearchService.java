package cn.lxing.service;

import cn.lxing.dao.ArticleDao;
import cn.lxing.entity.Article;
import cn.lxing.entity.Page;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**通过lucene查询需要检索的文章
 * Created by lxing on 2017/3/12.
 */
@Service
public class SearchService {
    @Autowired
    private ArticleDao articleDao;
    private String tableName = "articletest";

    //普通查询
    public List<Article> searchPage(String directoryPath, String query, int pageIndex) {
        int pageSize = 10;
        if (pageIndex == 0) {
            System.out.print("pageIndex == 0");
            return null;
        }
        List<Article> list = null;
        try {
            IndexSearcher searcher = createIndexSearcher(directoryPath);
            Analyzer analyzer = new IKAnalyzer();
            QueryParser parser = new QueryParser("content", analyzer);
            Query q = parser.parse(query);
            TopDocs tds = searcher.search(q, 500);
            // 注意 此处把500条数据放在内存里。
            ScoreDoc[] sds = tds.scoreDocs;
            int start = (pageIndex - 1) * pageSize;
            int end = pageIndex * pageSize;
            if (end > sds.length)
                end = sds.length;
            list = new ArrayList<Article>();
            // 高亮
            int maxNumFragmentsRequired = 2;
            QueryScorer scorer = new QueryScorer(q, "content");
            Formatter formatter = new SimpleHTMLFormatter("<font color=\"red\">", "</font>");
            Highlighter highlighter = new Highlighter(formatter, scorer);
            highlighter.setTextFragmenter(new SimpleFragmenter(70));
            for (int i = start; i < end; i++) {
                Document doc = searcher.doc(sds[i].doc);
                Article article = articleDao.searchOnHbaseByRkey("CSDN_article", doc.get("ROWKEY"));
                String text = article.getContent();
                TokenStream tokenStream = analyzer.tokenStream("content", text);
                String result;
                try {
                    result = highlighter.getBestFragments(tokenStream, text, maxNumFragmentsRequired, "...");
                    article.setContent(result);
                } catch (InvalidTokenOffsetsException e) {
                    article.setContent(null);
                    System.out.println("高亮失败");
                    e.printStackTrace();
                }
                list.add(article);
            }
            searcher.getIndexReader().close();

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

//    //分页查询
//    public void queryByPage(String directoryPath, Page<Article> page) {
//        if (page.getPageIndex() < 0 || page.getStart() < 0 || page.getPageSize() < 0)
//            return;
//        List<Article> list = null;
//        try {
//            IndexSearcher searcher = createIndexSearcher(directoryPath);
//            Analyzer analyzer = new IKAnalyzer();
//            QueryParser parser = new QueryParser("content", analyzer);
//            Query q = parser.parse(page.getQueryString());
//            TopDocs tds = searcher.search(q, 500);
//            // 注意 此处把500条数据放在内存里。
//            ScoreDoc[] sds = tds.scoreDocs;
//            page.setTotalRecord(sds.length);
//            int start = page.getStart();
//            int end = (page.getPageIndex() + 1) * page.getPageSize();
//            if (end > sds.length)
//                end = sds.length;
//            list = new ArrayList<Article>();
//            // 高亮
//            int maxNumFragmentsRequired = 2;
//            QueryScorer scorer = new QueryScorer(q, "content");
//            Formatter formatter = new SimpleHTMLFormatter("<font color=\"red\">", "</font>");
//            Highlighter highlighter = new Highlighter(formatter, scorer);
//            highlighter.setTextFragmenter(new SimpleFragmenter(70));
//            for (int i = start; i < end; i++) {
//                //开始查询
//                Document doc = searcher.doc(sds[i].doc);
//                Article article = articleDao.searchOnHbaseByRkey("CSDN_article", doc.get("ROWKEY"));
//                String text = article.getContent();
//                TokenStream tokenStream = analyzer.tokenStream("content", text);
//                String result;
//                try {
//                    result = highlighter.getBestFragments(tokenStream, text, maxNumFragmentsRequired, "...");
//                    article.setContent(result);
//                } catch (InvalidTokenOffsetsException e) {
//                    article.setContent(null);
//                    e.printStackTrace();
//                }
//                list.add(article);
//            }
//            page.setList(list);
//            searcher.getIndexReader().close();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    //searchList分页查询
    public void queryByPage(String directoryPath, Page<Article> page) {
        if (page.getPageIndex() < 0 || page.getStart() < 0 || page.getPageSize() < 0)
            return;
        List<Article> articleList = null;
        try {
            IndexSearcher searcher = createIndexSearcher(directoryPath);
            Analyzer analyzer = new IKAnalyzer();
            QueryParser parser = new QueryParser("content", analyzer);
            Query q = parser.parse(page.getQueryString());
            TopDocs tds = searcher.search(q, 500);
            // 注意 此处把500条数据放在内存里。
            ScoreDoc[] sds = tds.scoreDocs;
            page.setTotalRecord(sds.length);
            int start = page.getStart();
            int end = (page.getPageIndex() + 1) * page.getPageSize();
            if (end > sds.length)
                end = sds.length;
            // 高亮
            int maxNumFragmentsRequired = 2;
            QueryScorer scorer = new QueryScorer(q, "content");
            Formatter formatter = new SimpleHTMLFormatter("<font color=\"red\">", "</font>");
            Highlighter highlighter = new Highlighter(formatter, scorer);
            highlighter.setTextFragmenter(new SimpleFragmenter(70));
            List<String> listRowkey = new ArrayList<>();
            //ROWKEY list
            for (int i = start; i < end; i++) {
                //开始查询
                Document doc = searcher.doc(sds[i].doc);
                listRowkey.add(doc.get("rowkey"));
            }
            //通过rowkey集合查找 文章集合
            articleList = articleDao.searchByRkeyList(tableName,listRowkey);
            //文章集合高亮
            for(Article article:articleList){
                String text = article.getContent();
                TokenStream tokenStream = analyzer.tokenStream("content", text);
                String result;
                try {
                    result = highlighter.getBestFragments(tokenStream, text, maxNumFragmentsRequired, "...");
                    article.setContent(result);
                } catch (InvalidTokenOffsetsException e) {
                    article.setContent(null);
                    e.printStackTrace();
                }
            }
            page.setList(articleList);

            searcher.getIndexReader().close();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取 IndexSearcher
    private IndexSearcher createIndexSearcher(String directoryPath) throws IOException {
        File root = new File(directoryPath);
        int count = 0;
        List<File> listFile = new ArrayList<File>();
        for (File f : root.listFiles()) {
            if (f.isDirectory()) {
                count++;
                listFile.add(f);
                continue;
            }
        }
        if (count == 0)
            return new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(directoryPath, new String[0]))));
        IndexReader[] indexReader = new IndexReader[count];
        for (int i = 0; i < count; i++) {
            indexReader[i] = DirectoryReader.open(FSDirectory.open(listFile.get(i).toPath()));
        }
        MultiReader multiReader = new MultiReader(indexReader);
        IndexSearcher indexSearcher = new IndexSearcher(multiReader);
        return indexSearcher;
    }
}
