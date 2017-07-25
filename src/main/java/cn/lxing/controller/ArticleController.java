package cn.lxing.controller;

import cn.lxing.entity.Article;
import cn.lxing.entity.Page;
import cn.lxing.service.SearchService;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by lxing on 2017/3/11.
 */

@Controller
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private SearchService searchService;

    @RequestMapping("/search")
    public String view(String queryString, HttpServletRequest request) {
        request.setAttribute("queryString", queryString);
        return "content";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Object list(Page<Article> page, HttpServletRequest request) {
        System.out.println(page);
        String directoryPath = "D:\\index";
        searchService.queryByPage(directoryPath,page);
        return page;
    }


    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        return "index";
    }
}
