package cn.xw.webmagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * @author xw
 * @date 2019/6/14 15:54
 */
public class JobProcessor implements PageProcessor {

    //解析页面
    public void process(Page page) {
        //解析返回的数据page 并且把解析的数据放入resultItems中
        page.putField("div",page.getHtml().css("div.imgtextlist2 dl").all());

        //使用xpath表达式
        page.putField("div1",page.getHtml().xpath("//div[@class=imgtextlist2]/dl"));

        //使用正则表达式
        page.putField("div2",page.getHtml().css("div.imgtextlist2 dl").regex(".*军运.*").all());

        //处理结果api
        page.putField("div3",page.getHtml().css("div.imgtextlist2 dl").regex(".*军运.*").get());
        page.putField("div4",page.getHtml().css("div.imgtextlist2 dl").regex(".*军运.*").toString());

        //获取链接
        //page.addTargetRequests(page.getHtml().css("div.imgtextlist2 dl dt").links().all());
        //以获取到的链接的页面作为解析页面进行再一次解析
        //page.putField("url",page.getHtml().css("div.videodetail h4").all());

        page.addTargetRequest("http://wuhan.yiwuzhishu.cn/column/SY");
        page.addTargetRequest("http://wuhan.yiwuzhishu.cn/column/SY");
        page.addTargetRequest("http://wuhan.yiwuzhishu.cn/column/SY");
    }

    private Site site = Site.me()
            .setTimeOut(100000) //设置超时时间
            .setRetrySleepTime(10*1000) //设置重试间隔时间
            .setRetryTimes(3)  //设置重试次数
            .setCharset("utf8") //设置编码
            ;
    public Site getSite() {
        return site;
    }

    //主函数执行爬虫
    public static void main(String[] args) {
        Spider spider = Spider.create(new JobProcessor())
                .addUrl("http://wuhan.yiwuzhishu.cn/column/SY") // 设置要爬取的链接
                //.addPipeline(new FilePipeline("D:\\IdeaProjects\\crawler-webmagic\\result")) //设置输出路径
                .thread(5)  //设置线程数量
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000))) //设置布隆去重过滤器
                ;

        Scheduler scheduler = spider.getScheduler();

        spider.run();
    }
}
