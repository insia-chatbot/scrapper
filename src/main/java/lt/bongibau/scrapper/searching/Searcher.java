package lt.bongibau.scrapper.searching;

import lt.bongibau.scrapper.ScrapperLogger;
import lt.bongibau.scrapper.database.Data;
import lt.bongibau.scrapper.database.DatabaseInterface;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class Searcher extends Thread {
    public enum Phase {
        WORKING,
        IDLE
    }

    public interface Observer {
        /**
         * Called when the searcher finds links on a page,
         * basically when the searcher is working and finished
         * one loop of searching
         *
         * @param baseUrl Base URL of the page, where the links were found
         * @param links List of links found on the page, links are relative to the base URL
         *              and are not formatted, they are just the href attribute values.
         *              They should be formatted before using them.
         */
        void notify(URL baseUrl, List<String> links);
    }

    private final List<URL> heap = new LinkedList<>();

    private final List<Searcher.Observer> observers = new LinkedList<>();

    private boolean running = false;

    private Phase phase = Phase.IDLE;

    @Override
    public void run() {
        while (this.isRunning()) {
            this.setPhase(Phase.WORKING);

            URL url = this.pop();
            if (url == null) {
                this.setPhase(Phase.IDLE);
                ScrapperLogger.log("Heap Size: " + this.getWorkload() + ". No work found, sleeping...");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    ScrapperLogger.log(Level.SEVERE, "Failed to sleep.", e);
                }

                continue;
            }

            ScrapperLogger.log("Heap Size: " + this.getWorkload() + ". Processing URL: " + url);

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();

                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.connect();
            } catch (IOException e) {
                ScrapperLogger.log(Level.SEVERE, "Failed to connect to: " + url, e);
            }

            int status = 0;
            try {
                status = connection.getResponseCode();
            } catch (IOException e) {
                ScrapperLogger.log(Level.SEVERE, "Failed to get response code from: " + url, e);
                continue;
            }

            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM) {
                String location = connection.getHeaderField("Location");

                ScrapperLogger.log("Redirecting to: " + location + ", adding to heap and notifying all.");

                this.notifyAll(url, List.of(location));
                continue;
            }

            if (status != HttpURLConnection.HTTP_OK) {
                ScrapperLogger.log(Level.WARNING, "Failed to connect to: " + url + ", status: " + status);
                continue;
            }

            StringBuilder content = new StringBuilder();
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

            } catch (IOException e) {
                ScrapperLogger.log(Level.SEVERE, "Failed to read content from: " + url, e);
            }


            Document document = Jsoup.parse(content.toString());
            String lastModified = connection.getHeaderField("Last-Modified");
            LocalDateTime modificationDate = null;

            if (lastModified != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME; // Format RFC 1123
                    modificationDate = LocalDateTime.ofInstant(
                            formatter.parse(lastModified, Instant::from),
                            ZoneId.of("UTC") // Adapter si nécessaire
                    );
                    ScrapperLogger.log("Last modified: " + modificationDate);
                } catch (DateTimeParseException e) {
                    ScrapperLogger.log(Level.WARNING, "Failed to parse Last-Modified date: " + lastModified, e);
                }
            }else ScrapperLogger.log(Level.INFO, "No Last-Modified header found for URL: " + url);

            String pageText;
            String contentType = connection.getContentType();
            if(contentType != null && contentType.contains("application/pdf")) {
                try {
                    InputStream inputStream= url.openStream();
                    PDDocument documentPdf = PDDocument.load(inputStream);
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    pageText = pdfStripper.getText(documentPdf);
                    documentPdf.close();
                    inputStream.close();
                    ScrapperLogger.log("The PDF content has been read successfully.");
                }catch (IOException e) {
                    ScrapperLogger.log(Level.SEVERE, "Failed to read PDF content from: " + url, e);
                    continue;

                }
            } else {
                pageText = document.text();
            }
            assert lastModified != null;
            Data data = new Data(url.toString(), pageText, modificationDate, LocalDateTime.now());
            DatabaseInterface.getInstance().insertData(data);

            try {
                List<String> links = document.select("a").stream().map((a) -> a.attr("href")).toList();
                this.notifyAll(url, links);
            } catch (Exception e) {
                ScrapperLogger.log(Level.SEVERE, "Failed to parse content from: " + url, e);
            }

            connection.disconnect();



        }

        ScrapperLogger.log("Searcher stopped.");


    }

    public synchronized void setPhase(Searcher.Phase phase) {
        this.phase = phase;
    }

    public synchronized Searcher.Phase getPhase() {
        return phase;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized void push(URL url) {
        heap.add(url);
    }

    public synchronized void notifyAll(URL baseUrl, List<String> links) {
        for (Searcher.Observer observer : observers) {
            observer.notify(baseUrl, links);
        }
    }

    public synchronized void subscribe(Searcher.Observer observer) {
        observers.add(observer);
    }

    public synchronized void unsubscribe(Searcher.Observer observer) {
        observers.remove(observer);
    }

    public synchronized boolean hasWork() {
        return !heap.isEmpty();
    }

    public synchronized int getWorkload() {
        return heap.size();
    }

    @Nullable
    public synchronized URL pop() {
        return heap.isEmpty() ? null : heap.removeFirst();
    }
}
