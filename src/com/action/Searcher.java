package com.action;

import com.dao.Article;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Searcher extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");//servlet中也要此项，否则取值乱码


        String queryStr =request.getParameter("keyWord");
        //System.out.println(queryStr);

        Directory dir = FSDirectory.open(new File("index/"));
        IndexReader reader = IndexReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        //searcher.setDefaultFieldSortScoring(true, false);   //Tell IndexSearcher we want scores for each hit


        IndexReader newReader = reader.reopen();
        if (reader != newReader) {                         // the IndexReader.reopen method is a resource efficient means of obtaining a new
                                                            //IndexReader that covers all changes to the index but shares resources with the
                                                            //current reader when possible.
            reader.close();
            reader = newReader;
            searcher = new IndexSearcher(reader);
        }



        QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "contents", new CJKAnalyzer(Version.LUCENE_CURRENT));
        Query luceneQuery = null;


        try {
            luceneQuery = parser.parse(queryStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        TopDocs docs = searcher.search(luceneQuery,10);   //TopDocs is a object that represent the returned results.
        searcher.close();                                     //The TopDocs.scoreDocs attribute is an array containing the top n matches.

        //retrieve the title from the first document found.
        //Document d = searcher.doc(docs.scoreDocs[0].doc); //doc (int number) is the document ID that can be used to retrieve the stored fields for that document by calling
                                                                                        //IndexSearcher.document(doc) .
        //String title=d.get("title");




        HttpSession session = request.getSession(true);
        List<Article> articleList = new ArrayList<Article>();

        for(int i = 0; i < docs.totalHits; i++){

            Article article = new Article();

            ScoreDoc match = docs.scoreDocs[i];
            Document doc = searcher.doc(match.doc);
            //article.setTitle(doc.get("title"));
            //article.setSummary(doc.get("summary"));
            article.setHref(doc.get("href"));

            articleList.add(article);

            System.out.println(doc.get("title"));
            System.out.println(doc.get("summary"));
            System.out.println(doc.get("href"));


            String title = highLight(doc.get("title"),luceneQuery);

            if (title != null){
                    article.setTitle(title);
                }
            else {
                    article.setTitle(doc.get("title"));
                }


            String summary = highLight(doc.get("summary"),luceneQuery);

            if (summary != null){
                    article.setSummary(summary);
                }
                else {
                    article.setSummary(doc.get("summary"));
                }
            }


            Article art = new Article();
            art.setArticleList(articleList);
            session.setAttribute("article",art);

            request.getRequestDispatcher("/index.jsp").forward(request, response);

    }

    private String highLight(String field,Query luceneQuery){


        QueryScorer scorer = new QueryScorer(luceneQuery, "contents");
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(formatter,scorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));

        TokenStream tokens = new  CJKAnalyzer(Version.LUCENE_CURRENT).tokenStream("contents",
                new StringReader(field));

        String fragment = null;
        try {
            fragment = highlighter.getBestFragment(tokens, field);
        } catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fragment;
    }
}


