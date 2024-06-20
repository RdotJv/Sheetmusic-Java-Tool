package com.example.services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;
import java.time.Duration;
import java.util.stream.Collectors;
import com.example.repositories.SheetmusicRepo;

@Service
public class WsService {
    @Autowired
    public WebDriver driver;

    @Autowired
    private SheetmusicRepo repo;

    private ArrayList<String> resultLinks = new ArrayList<>();          //links kept in case user is dissatisfied
    private String url = "https://www.google.com/search?q=site:imslp.org+";
    public record Song(int id,String composerName ,String songName, String pdfUrl) {}
    private boolean resultLinksEmpty = true;


    public Object[] defaultSearch(String composerQuery, String songQuery) {
        if (resultLinksEmpty) {
            HashMap<String, String> downloadsTracker = initLinksAndNav(composerQuery, songQuery);
            ArrayList<String> descendingOrderDownloadCount = sortDlCount(downloadsTracker);  //returns highest download counts in an array sorted highest to lowest
            for (String i : descendingOrderDownloadCount){
                resultLinks.add(downloadsTracker.get(i));
            }
        }

        //String composerName = driver.findElement(By.xpath("//*[@id=\"firstHeading\"]/a")).getText();

        String currentUrl = this.resultLinks.getFirst();
        driver.get(currentUrl);
        System.out.println(currentUrl);
        resultLinks.removeFirst();
        resultLinksEmpty = resultLinks.isEmpty();

        String linkForPdf = getPdfLink();

        Song result = new Song(0 ,composerQuery, songQuery.toUpperCase(), linkForPdf);
        repo.addSong(result);
        System.out.println(result);

        if(resultLinksEmpty){
            return new Object[] {result, resultLinksEmpty};
        }
        return new Object[] {result, resultLinksEmpty};
    }


    public ArrayList<String> sortDlCount(HashMap<String, String> results){
        Set<Integer> tempkeyset = Set.copyOf(results.keySet()).stream().map(Integer::parseInt).collect(Collectors.toSet());
        ArrayList<Integer> intTemp = new ArrayList<>(tempkeyset);
        intTemp.sort(Comparator.reverseOrder());    //descending order arraylist of download counts
        ArrayList<String> highestDlCountArr = new ArrayList<>();
        for (int i: intTemp) {
            highestDlCountArr.add(String.valueOf(i));
        }
        return highestDlCountArr;
    }

    public String getPdfLink() {
        driver.findElement(By.xpath("//*[@id=\"file\"]/a/img")).click();

        try {
            driver.findElement(By.xpath("//*[@id=\"wiki-body\"]/div[2]/center/a")).click();          //accepting TOS
        } catch (Exception e) {
            System.out.println("THIS ERROR ONLY ARISES WHEN USER ASKS FOR MORE RESULTS AND THE PROGRAM ATTEMPTS TO ACCESS THE" +
                    "TOS ELEMENT AGAIN WHEN IT NO LONGER EXISTS "+e);
        }

        String linkForPdf;
        if (!Objects.equals(driver.findElement(By.xpath("/html")).getAttribute("class"), "client-js")) {
            linkForPdf = driver.getCurrentUrl();
        } else {
            linkForPdf = driver.findElement(By.xpath("//*[@id=\"sm_dl_wait\"]")).getAttribute("data-id");
        }
        return linkForPdf;
    }

    public HashMap<String, String> initLinksAndNav (String composerQuery, String songQuery) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(url+(composerQuery+songQuery).replace(" ", "%20"));
        WebElement firstImslpResult = wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("https://imslp.org")));
        firstImslpResult.click();

        List<WebElement> sheets = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\"tabScore1\"]/div")));
        //sheets includes all except the last 2 results (which are usually copyrighted)
        int n = 0;
        HashMap<String, String> downloadsTracker = new HashMap<>();
        for (int i=0; i<sheets.size(); i++) {
            String link = driver.findElement(By.xpath("//*[@id='tabScore1']/div[" + (++n) + "]/div/div/p/span/a")).getAttribute("href");
            String downloadCount = driver.findElement(By.xpath("//*[@id='tabScore1']/div[" + n + "]/div/div/p/span/span[4]/a")).getText();
            if (!Objects.equals(downloadCount, "")) {
                downloadsTracker.put(downloadCount, link);
            }
        }
        return downloadsTracker;
    }

    public void downloadPDF(Song song) {
        try {
            URL url = new URL(song.pdfUrl());
            InputStream inputStream = url.openStream();
            FileOutputStream fileOutputStream = new FileOutputStream("C:/Users/USER/Desktop/mysheetmusic/"+song.composerName()+song.songName()+".pdf");
            byte[] bytes = new byte[1028];
            int len;
            while ((len=inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, len);
            }
            fileOutputStream.close();
            inputStream.close();
        }
        catch (Exception e) {
            System.out.println("Error while downloading pdf "+e);
        }


    }
}
