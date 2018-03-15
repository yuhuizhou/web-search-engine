package com.dao;

import java.util.ArrayList;
import java.util.List;


    public class Article
    {
        private List<Article> articleList;
       // private List<GoodsClassify>  goodsClassifyList;    //多表联立时使用

       // private int commodity_number = -1; //商品编号
        private String title;     //商品名字
        private String summary;     //产地
        private String href;      //商品图片
        private int totalhit;     //商品分类代号

        public Article()
        {
            articleList = new ArrayList<Article>();
            //goodsClassifyList = new ArrayList<GoodsClassify>();
        }


        public List<Article> getArticleList() {
            return articleList;
        }

        public void setArticleList(List<Article> articleList) {
            this.articleList = articleList;
        }

        public int getTotalhit()
        {
            return totalhit;
        }

        public void setTotalhit(int totalhit)
        {
            this.totalhit = totalhit;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getSummary()
        {
            return summary;
        }

        public void setSummary(String summary)
        {
            this.summary = summary;
        }
        public void setHref(String href){ this.href=href; }

        public String getHref()
        {
            return href;
        }




    }

