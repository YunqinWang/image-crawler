package com.eulerity.hackathon.imagefinder;

/** The exception is thrown when the link visiting is not valid or can't be connected.
 *
 * @author ywang */
public class UnableToConnectException extends RuntimeException {

    private static final long serialVersionUID= -3776773574147837081L;

    public UnableToConnectException(String url) {
        System.out.println("Can't connect to this page. The link: " + url + " might be invalid");
    }
}
