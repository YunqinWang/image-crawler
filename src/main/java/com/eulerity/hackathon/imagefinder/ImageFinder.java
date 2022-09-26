package com.eulerity.hackathon.imagefinder;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Start to crawl for images on a page.<br>
 * It dives into links contained on this page, links on the sub pages and so forth.<br>
 * The maximum number of links to visit is limited by MAX_LINK_TO_CRAWL. <br>
 * The maximum depth of a link to visit is limited by MAX_DEPTH. <br>
 * To be a friendly crawler, it waits several seconds before crawling on another page, <br>
 * which is defined by WAIT_BEFORE_NEXT_PAGE.<br>
 * To speed up response times, it is implemented with multi-threads.<br>
 * The image collecting and link collecting happen at the same time.<br>
 * Images gained don't include logo.
 *
 * @author ywang */
@WebServlet(name= "ImageFinder", urlPatterns= { "/main" })
public class ImageFinder extends HttpServlet {
    private static final long serialVersionUID= 1L;

    protected static final Gson GSON= new GsonBuilder().create();

    /** result of image list */
    public static LinkedList<String> testImages= new LinkedList<>();

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.reset();
        resp.setContentType("text/json");
        String path= req.getServletPath();
        String url= req.getParameter("url");
        System.out.println("Got request of:" + path + " with query param:" + url);

        PagesTree toCrawl= new PagesTree();
        PageNode start= new PageNode(url, 0);

        // start crawling the page and wait for all the threads finished
        try {
            toCrawl.webCrawl(start, testImages);
            for (Thread thread : toCrawl.getAllCollectingThread())
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        resp.getWriter().print(GSON.toJson(testImages));
        System.out.println("--------all end-------");
    }

    public static void main(String args[]) throws InterruptedException {
        String url= args[0];

        PageNode start= new PageNode(url, 0);
        PagesTree toCrawl= new PagesTree();

        // start crawling the page and wait for all the threads finished
        toCrawl.webCrawl(start, testImages);
        for (Thread thread : toCrawl.getAllCollectingThread()) thread.join();

        System.out.println("----------------------");
        System.out.println("total img number: " + testImages.size());
        System.out.println("--------all end-------");
    }
}
