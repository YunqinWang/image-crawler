package com.eulerity.hackathon.imagefinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/** An instance is a queue that stores all links to be visited.<br>
 * Links are all unique and can be connected.<br>
 * Once the link is visited, it will be removed from the queue, <br>
 * so the next link to visit is always the first item in the queue.<br>
 * Any new links discovered are added at the end of the queue.
 *
 * @author ywang */
public class DropQueue {

    /** Maximum depth of a page to visit.<br>
     * When MAX_DEPTH =0, it means to only crawl on the start page, and never goes deeper. */
    private static final int MAX_DEPTH= 5;

    /** Maximum number of links to crawl.<br>
     * When MAX_LinkToCrawl=1, it means crawling only on the given page. */
    static final int MAX_LINK_TO_CRAWL= 20;

    private int numberOfVisitedLink= 0;

    static boolean hitMaxDepth;
    static boolean noMorePage;

    // all visited links in sequence
    private LinkedList<PageNode> allVisitedLinks= new LinkedList<>();
    // all visited links
    private HashSet<String> discoveredLinksCheck= new HashSet<>();
    // all discovered links
    private HashMap<String, Boolean> allDiscoveredLinks= new HashMap<>();

    private LinkedList<String> testImages;// all found images links
    private HashSet<String> testImagesHashSet= new HashSet<>();

    // all links discovered, but not visited yet
    private LinkedList<PageNode> queue= new LinkedList<>();

    /** Constructor: a new queue to store all links to visit
     *
     * @param start                is the first page to crawl on
     * @param testImages           storing all found images
     * @param testImagesHashSet    storing all found images, used to check if the image has been
     *                             found before
     * @param discoveredLinksCheck storing all discovered links, used to check if the link has been
     *                             found before */
    DropQueue(PageNode start, LinkedList<String> testImages) {
        queue.add(start);
        this.testImages= testImages;
        hitMaxDepth= false;
    }

    /** Wait for the queue to contain at least one page.<br>
     * Then visit it, remove it from the queue and get all images links on that page. */
    public synchronized void take() {
        while (queue.size() == 0) try {
            System.out.println("Take waiting");
            wait();
        } catch (InterruptedException e) {
            System.out.println("No item to take from queue");
        }

        PageNode firstPage= queue.getFirst();
        queue.removeFirst();
        System.out.println("Taking the first page " + firstPage.getUrl());

        discoveredLinksCheck.add(firstPage.getUrl());
        allDiscoveredLinks.put(firstPage.getUrl(), true);
        allVisitedLinks.add(firstPage);
        System.out.println("AllVisitLinks size: " + allVisitedLinks.size());

        firstPage.getImageLink(testImages, testImagesHashSet);

        notifyAll();
    }

    /** Wait for the first item in the queue is different from the last peek. <br>
     * Then crawl on the first item for all the links on that page. <br>
     * If the maximum of diving depth is hit, no need to collect links on that page. */
    public synchronized void put() {
        while (allVisitedLinks.size() < numberOfVisitedLink + 1 && !noMorePage) try {
            System.out.println("Put waiting");
            wait();
        } catch (InterruptedException e) {
            System.out.println("No item to peek");
        }

        PageNode firstPage= allVisitedLinks.get(numberOfVisitedLink);

        if (firstPage == null) return;
        numberOfVisitedLink++ ;

        System.out.println("----------------------");
        System.out.println("Getting more links on the page: " + firstPage.getUrl());

        if (discoveredLinksCheck.size() > MAX_LINK_TO_CRAWL) return;
        if (firstPage.getDepth() > MAX_DEPTH) {
            hitMaxDepth= true;
            return;
        }

        firstPage.getMoreLinks(allDiscoveredLinks, discoveredLinksCheck);

        if (firstPage.end) {
            noMorePage= true;
            return;
        }

        for (PageNode moreLink : firstPage.otherLinks) {
            System.out.println("Adding new link to the queue: " + moreLink.getUrl());
            queue.add(moreLink);
        }
        System.out.println("----------------------");

        notifyAll();
    }
}
