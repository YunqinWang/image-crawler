package com.eulerity.hackathon.imagefinder;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/** An instance is a page tree that uses BFS to traverse all the pages in the tree.
 *
 * @author ywang */
public class PagesTree {

    /** Wait several seconds before connecting to the next page */
    private static final int WAIT_BEFORE_NEXT_PAGE= 2;

    int count;
    private HashSet<Thread> allImageCollectThread;

    /** Constructor: create a new pageTree, set number of crawled pages to 0. */
    PagesTree() {
        allImageCollectThread= new HashSet<>();
        count= 0;
    }

    /** Start image crawling (BFS algorithm) from the startPage.<br>
     * When it is finished, all found images are stored in the testImages.
     *
     * @param startPage  start from this page, root of the tree
     * @param testImages a list to store found images
     * @throws InterruptedException */
    public void webCrawl(PageNode startPage, LinkedList<String> testImages)
        throws InterruptedException {

        testImages.clear();// clear the result list first

        // If the start link can't be connected, end crawling
        if (!startPage.getValid()) return;

        DropQueue dropQueue= new DropQueue(startPage, testImages);

        // If the visiting has reached limits of either
        // max number of links or max depth of diving,
        // end crawling
        while (count < DropQueue.MAX_LINK_TO_CRAWL && !DropQueue.hitMaxDepth) {

            count++ ;
            System.out.println("main thread id" + Thread.currentThread().getId());

            // New thread to collect images
            ImgLinkCollector imgLinkCollector= new ImgLinkCollector(dropQueue);
            Thread imgInstance= new Thread(imgLinkCollector);
            imgInstance.start();
            if (!allImageCollectThread.contains(imgInstance))
                allImageCollectThread.add(imgInstance);

            // New thread to collect sub pages links
            MoreLinkCollector moreLinkCollector= new MoreLinkCollector(dropQueue);
            Thread moreLinkInstance= new Thread(moreLinkCollector);
            moreLinkInstance.setDaemon(true);// this thread dies when the other threads all end
            moreLinkInstance.start();

            if (DropQueue.noMorePage) return;

            TimeUnit.SECONDS.sleep(WAIT_BEFORE_NEXT_PAGE);
        }
    }

    /** @return all used threads */
    HashSet<Thread> getAllCollectingThread() {
        return allImageCollectThread;
    }
}
