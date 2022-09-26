package com.eulerity.hackathon.imagefinder;

/** It collects images on a page.
 *
 * @author ywang */
public class MoreLinkCollector implements Runnable {

    private final DropQueue dropQueue;

    /** Constructor: create a new SubPage through PageNode
     *
     * @param webUrl is the url of this page */
    MoreLinkCollector(DropQueue queue) {
        dropQueue= queue;
    }

    /** Links of images contained on this page <br>
     * are added to the embeddedLink. */
    @Override
    public void run() {
        dropQueue.put();
        System.out.println("link collecting, thread id " + Thread.currentThread().getId());
    }
}
