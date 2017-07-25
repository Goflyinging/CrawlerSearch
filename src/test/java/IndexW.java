import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Created by lxing on 2017/6/1.
 */
public class IndexW {
    
    public void addIndex(Directory dir, String content) throws IOException,
                                                        ParseException {
        // Directory dir= FSDirectory.open(Paths.get("/tmp/testindex"));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter inWriter = new IndexWriter(dir, config);
        Document doc = new Document();
        doc.add(new StringField("fiedname", content, Field.Store.YES));
        inWriter.addDocument(doc);
        inWriter.close();
    }
    
    public void show(Directory dir, String query) throws Exception {
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new IKAnalyzer();
        QueryParser parser = new QueryParser("fiedname", analyzer);
        Query q = parser.parse(query);
        TopDocs tds = searcher.search(q, 500);
        // 注意 此处把500条数据放在内存里。
        ScoreDoc[] sds = tds.scoreDocs;
        for (int i = 0; i < sds.length; i++) {
            // 开始查询
            Document doc = searcher.doc(sds[i].doc);
            System.out.println(doc.toString());
            Iterator<IndexableField> iterator = doc.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            System.out.println("2--------" + doc.get("fiedname"));
        }
    }
    
    public static void mergeIndex(Path from, Path to, Analyzer analyzer) {
        IndexWriter indexWriter = null;
        try {
            final Directory indexDir = FSDirectory.open(to);
            IndexWriterConfig indexWriterConfig =
                                                new IndexWriterConfig(analyzer);
            // indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            
            System.out.println("正在合并索引文件!\t ");
            indexWriter = new IndexWriter(indexDir, indexWriterConfig);
            Directory fromDir = FSDirectory.open(from);
            indexWriter.addIndexes(fromDir);
            IndexReader iw1 = DirectoryReader.open(fromDir);
            //indexWriter.addIndexes(iw1);

            indexWriter.forceMerge(1);
            indexWriter.close();
            System.out.println("已完成合并!\t ");
        }
        catch (Exception e) {
            System.out.println("合并索引出错！");
            e.printStackTrace();
        }
        finally {
            try {
                if (indexWriter != null)
                    indexWriter.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                
            }
            
        }
        
    }
    
    public static void main(String[] args) throws Exception {
        IndexW indexW = new IndexW();
        Directory dir1 = FSDirectory.open(Paths.get("D:/tmp/1"));
        Directory dir2 = FSDirectory.open(Paths.get("D:/tmp/2"));
        
        // indexW.addIndex(dir1,"力量");
        // indexW.addIndex(dir2,"知识");
        // indexW.show(dir1,"力量");
        // indexW.show(dir2,"知识");
        
        Path from = Paths.get("D:/tmp/1");
        Path to = Paths.get("D:/tmp/2");
        mergeIndex(from, to, new IKAnalyzer(true));
        indexW.show(dir2, "力量");
        indexW.show(dir2, "知识");
    }
    
}
