# ImageFinder Goal
The goal of this task is to perform a web crawl on a URL string provided by the user. From the crawl, you will need to parse out all of the images on that web page and return a JSON array of strings that represent the URLs of all images on the page. [Jsoup](https://jsoup.org/) is a great basic library for crawling and is already included as a maven dependency in this project, however you are welcome to use whatever library you would like.

### Basic Functions
- A web crawler that can find all images on the web page(s) that it crawls.
- Crawl sub-pages to find more images.
- Implement multi-threading so that the crawl can be performed on multiple sub-pages at a time.
- Keep the crawl within the same domain as the input URL.
- Avoid re-crawling any pages that have already been visited.

### Extra Functions
- A friendly crawler - not to get banned from the site by performing too many crawls.
- Try to detect what images might be considered logos.

## Structure
The ImageFinder servlet is found in `src/main/java/com/eulerity/hackathon/imagefinder/ImageFinder.java`. 

The main landing page for this project can be found in `src/main/webapp/index.html`. . 

Finally, in the root directory of this project, the `pom.xml` contains the project configuration details used by maven to build the project.

## Running the Project

### Requirements
Before beginning, make sure you have the following installed and ready to use
- Maven 3.5 or higher
- Java 8
  - Exact version, **NOT** Java 9+ - the build will fail with a newer version of Java

### Setup
To start, open a terminal window and navigate to wherever you unzipped to the root directory `imagefinder`. To build the project, run the command:

>`mvn package`

If all goes well you should see some lines that end with "BUILD SUCCESS". When you build your project, maven should build it in the `target` directory. To clear this, you may run the command:

>`mvn clean`

To run the project, use the following command to start the server:

>`mvn clean test package jetty:run`

You should see a line at the bottom that says "Started Jetty Server". Now, if you enter `localhost:8080` into your browser, you should see the `index.html` welcome page! If all has gone well to this point, you're ready to begin!
