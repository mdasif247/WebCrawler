package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

public class Crawler {
    // used set so we dont visit same url
    HashSet<String> urlLinks;
    // max url that we can go from a page
    private int MAX_DEPTH=2;
    private Connection connection;

    public Crawler(){
        //set up the connection to mysql
        connection=DatabaseConnection.getConnection();
        urlLinks=new HashSet<>();
    }
    public void getpageLinksandText(String url,int depth){
        if(!urlLinks.contains(url)){
            //will go further only if we are able to add url
            if(urlLinks.add(url)){
                System.out.println(url);
            }
            try {
                //will get the html page as document
                Document document = Jsoup.connect(url).timeout(5000).get(); //will wait 5 sec for the connection
                //will store the text in document as substring if its length is >500 else will store as text
                String text=document.text().length()<501?document.text(): document.text().substring(0,500);
                System.out.println(text);
                //Insert into pages table
                //prepare statement helps us to insert values into a table
                PreparedStatement preparedStatement=connection.prepareStatement("Insert into pages values(?,?,?)");
                // 1st parameter will be title in document
                preparedStatement.setString(1,document.title());
                preparedStatement.setString(2,url);
                preparedStatement.setString(3,text);
                preparedStatement.executeUpdate();

                depth++;
                if(depth>MAX_DEPTH){
                    return;
                }
                //get hyperlinks available on the current page
                Elements  availableLinksOnPage = document.select("a[href]"); //css query for hyperlinks
                //recursively going for all the links
                for(Element currentLink : availableLinksOnPage){
                    getpageLinksandText(currentLink.attr("abs:href"),depth); //will select absolute href
                }

            }
            catch (IOException ioException){   // will catch the exception during accessing the hyperlink
                ioException.printStackTrace();
            }
            catch (SQLException sqlException){
                sqlException.printStackTrace();  //will catch exception while inserting into pages table
            }

        }
    }
    public static void main(String[] args) {
        Crawler crawler=new Crawler();
        crawler.getpageLinksandText("https://www.javatpoint.com/",0);


    }
}