public class Main {

    public static void main(String[] args) {

        WEB_crawler crawler = new WEB_crawler("http://www.mkyong.com/", 1, 4);
        crawler.startCrawl();

    }
}
