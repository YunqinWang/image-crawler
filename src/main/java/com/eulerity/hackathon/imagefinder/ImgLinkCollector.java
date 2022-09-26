package com.eulerity.hackathon.imagefinder;

/** It collects images on a page.
 *
 * @author ywang */
public class ImgLinkCollector implements Runnable {

    private final DropQueue dropQueue;

    ImgLinkCollector(DropQueue queue) {
        dropQueue= queue;
    }

    /** add links of images on this page to the imgLinks. */
    @Override
    public void run() {

        dropQueue.take();
        System.out.println("img collecting, thread id " + Thread.currentThread().getId());
    }
}
