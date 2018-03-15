package com.dao;

import java.util.List;

public class Page {

    private List<Article> articleList=null;


    public List<Article> getArticleList()
    {
        return articleList;
    }
    public void setArticleList(List<Article> articleList)
    {
        this.articleList = articleList;
    }
}
