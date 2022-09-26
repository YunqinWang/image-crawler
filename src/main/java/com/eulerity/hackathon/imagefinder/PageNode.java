package com.eulerity.hackathon.imagefinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/** An instance is a web page.
 *
 * @author ywang */
public class PageNode {

    /** Maximum number of pages to get on one single page. */
    private static final int MAX_LINK_ONE_PAGE= 20;

    private String webUrl;
    private String pageDomain;
    private boolean valid;
    private Document doc;
    private int depth;
    boolean end= false;

    LinkedList<PageNode> otherLinks= new LinkedList<>();

    /** Constructor: create a new web page and try to connect to the page.<br>
     * Valid will be set as false if it can not be connected.
     *
     * @param webUrl is the url of this page
     * @param depth  is the distance from the start page. 0 means it is the start page. */
    PageNode(String webUrl, int depth) {
        this.webUrl= webUrl;
        connectToAddress();
        if (valid)
            setDomain();
        this.depth= depth;
    }

    /** Connect to the page */
    void connectToAddress() {
        try {
            tryConnect();
        } catch (UnableToConnectException e) {
            valid= false;
        }
    }

    /** Set valid to true if the content type of this page is xml or +xml.<br>
     * Otherwise, throw UnableToConnectException and set valid to false.
     *
     * @throws UnableToConnectException */
    void tryConnect() throws UnableToConnectException {
        if (webUrl == null) {
            valid= false;
            return;
        }
        try {
            doc= Jsoup.connect(webUrl).get();
            valid= true;
        } catch (Exception e) {
            throw (new UnableToConnectException(webUrl));
        }
    }

    /** @return the page url. */
    String getUrl() {
        return webUrl;
    }

    /** Set domain of this page */
    void setDomain() {
        int firstDot= webUrl.indexOf('.');
        int secondDot= webUrl.indexOf('.', firstDot + 1);

        // if there is no second dot, it is not a valid link
        if (secondDot == -1) { valid= false; return; }

        pageDomain= webUrl.substring(firstDot + 1, secondDot);
    }

    /** @return domain of this page */
    String getDomain() {
        return pageDomain;
    }

    /** @return true if the page can be connected */
    boolean getValid() {
        return valid;
    }

    /** @return the depth of the page. 0 means it is the start page. */
    int getDepth() {
        return depth;
    }

    /** Check if the page is an entire page. If the link end with "#", it is just a section inside a
     * page, which is not wanted.
     *
     * @return true if it is an entire page rather an a section. */
    boolean anEntirePage() {
        int thirdSlash= webUrl.lastIndexOf("/");
        if (webUrl.length() == thirdSlash + 1) return true;
        char checkingChar= webUrl.charAt(thirdSlash + 1);

        if (checkingChar == '#')
            return false;
        return true;
    }

    /** Get all the image links on this page, not including logo.
     *
     * @param testImages        stores all found images.
     * @param testImagesHashSet stores all found images, used to check if the image has been found
     *                          before */
    void getImageLink(LinkedList<String> testImages, HashSet<String> testImagesHashSet) {

        Elements images= doc.select("img[src~=(?i)\\.(png|jpe?g)]");

        System.out.println("----------------------");
        System.out.println("Collecting images on: " + webUrl);

        for (Element image : images) {
            String url= image.absUrl("src");
            String className= image.className();
            String parentName= image.parent().className();

            if (url.length() > 0 && // check if the url is not empty
                !testImagesHashSet.contains(url) && // check if the image has been found before
                !className.contains("logo") && // check if it is inside a logo class
                !parentName.contains("logo") && // check if the parent tag marked as a logo class
                !url.contains("logo")) { // check if the name of the image contains logo

                testImagesHashSet.add(url);
                testImages.add(url);
                System.out.println(url);
            }
        }
        System.out.println("Finish getting image on: " + webUrl + ". It contains " +
            testImagesHashSet.size() + " images.");
        System.out.println("----------------------");
    }

    /** Get all valid links that are <br>
     * embedded, within the same domain and not discovered before on this page. It can get maximum
     * MAX_LINK_ONE_PAGE links on this page.
     *
     * @param allDiscoveredLinks      stores all found links
     * @param visitedLinksStringCheck stores all found links, used to check if the new link has been
     *                                found before. */
    void getMoreLinks(HashMap<String, Boolean> allDiscoveredLinks,
        HashSet<String> visitedLinksStringCheck) {

        Elements links= doc.select("a[href]");

        System.out.println("----------------------");
        System.out
            .println("Collecting linkg on: " + webUrl + ". It contains " + links.size() +
                " sub links in total.");

        int count= 0; // calculate how many links are found on the page

        for (Element link : links) {

            String moreLink= link.attr("abs:href");
            PageNode nextPage= new PageNode(moreLink, depth + 1);

            if (count >= MAX_LINK_ONE_PAGE) return;// end collecting if max links are got

            if (nextPage != null &&
                !visitedLinksStringCheck.contains(nextPage.getUrl()) && // check if the link has
                // been found before
                nextPage.valid &&  // check if the link is valid
                nextPage.pageDomain.equals(pageDomain) && // check if it has the same domain
                nextPage.anEntirePage()) {  // check if it is an entire page

                otherLinks.add(nextPage);
                allDiscoveredLinks.put(nextPage.getUrl(), false);
                visitedLinksStringCheck.add(nextPage.getUrl());

                count++ ;
            }
        }
        System.out.println("Finish getting " + count + " more links on this page.");
        System.out
            .println("CurrentDepth of the page on which links are collected is " + getDepth());
        System.out.println("----------------------");
        if (otherLinks.size() == 0)
            end= true;
    }

}
