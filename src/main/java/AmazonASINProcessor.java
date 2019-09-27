import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;


public class AmazonASINProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private static int lock  = 0;

    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来

        page.putField("ASIN", page.getHtml().xpath("//*[@id=\"productDetails_detailBullets_sections1\"]/tbody/tr[1]/td/text()").toString());
        // page.putField("Customers Reviews", page.getHtml().xpath("//div[@id=\"productDetails_db_sections\"]/tidyText()").toString());
        page.putField("Title", page.getHtml().xpath("/html/head/title/text()").toString());
        page.putField("Customers Reviews", page.getHtml().xpath("//*[@id=\"productDetails_detailBullets_sections1\"]/tbody/tr[2]/td/text()").toString());
        page.putField("Best Sellers Rank", page.getHtml().xpath("//*[@id=\"productDetails_detailBullets_sections1\"]/tbody/tr[3]/td/").toString());
        page.putField("Price", page.getHtml().xpath("//*[@id=\"price_inside_buybox\"]/text()").toString());
        if (page.getResultItems().get("Customers Reviews") == null) {
            //skip this page
            page.setSkip(true);
        } else {
            page.setSkip(false);
        }

        // 部分三：从页面发现后续的url地址来抓取
        System.out.println("new page: " + page.getHtml().links().regex(".*"));
        page.addTargetRequests(page.getHtml().links().regex("(https://www.amazon.com/.+/dp/.*)").all());
        lock++;
        System.out.println("get page: " + page.getResultItems().getRequest().getUrl());
        for (Map.Entry<String, Object> entry : page.getResultItems().getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        JsonHandler jsonHandler = new JsonHandler();
        jsonHandler.readProcess("ASIN");
        jsonHandler.printAll();

        Spider spider = Spider.create(new AmazonASINProcessor()).thread(2);
/*
        List<ResultItems> resultItems = spider.getAll(jsonHandler.URLList);
        for(ResultItems item : resultItems){
            System.out.println((item.getAll()));
        }
        spider.close();
*/
        spider.addUrl(jsonHandler.URLList.get(0))
                .addPipeline(new FilePipeline("C:\\Users\\Yiwen Gu\\Desktop\\output"))
                //开启5个线程抓取
               // .thread(10)
                //启动爬虫
                .run();
    }
}