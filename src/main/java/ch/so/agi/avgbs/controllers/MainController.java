package ch.so.agi.avgbs.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rometools.rome.feed.atom.Category;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.atom.Person;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndPerson;

import ch.so.agi.avgbs.dao.Av2GbMessageDAOImpl;
import ch.so.agi.avgbs.models.Av2GbMessage;

@RestController
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.awsBucketNameAv2Gb}")
    private String awsBucketNameAv2Gb;
    
    private String awsBaseUrl = "https://s3.eu-central-1.amazonaws.com/";

    @Autowired
    Av2GbMessageDAOImpl av2GbMessageDAO;
    
    @RequestMapping(value="/av2gb/rss.xml", method=RequestMethod.GET)
    public Channel rss() {
        List<Av2GbMessage> messages = av2GbMessageDAO.getAv2GbMessages();

        Channel channel = new Channel();
        channel.setFeedType("rss_2.0");
        channel.setTitle("AV2GB RSS Feed");
        channel.setDescription("Lieferungen: Amtliche Vermessung an das Grundbuch");
        channel.setLink("https://geo.so.ch/avgbs/av2gb");
  
        Date lastDate = messages.get(0).getImportdate();
        channel.setPubDate(lastDate);
 
        List<Item> items = new ArrayList<Item>();
        for (Av2GbMessage message : messages) {
            Item item = new Item();

            item.setLink(awsBaseUrl + awsBucketNameAv2Gb + "/" + message.getDateinameplan());
            item.setTitle(message.getMutationsnummerNummer() + " / " + message.getMutationsnummerNBIdent());
            item.setUri(awsBaseUrl + awsBucketNameAv2Gb + "/" + message.getDateinameplan());
            
            Description descr = new Description();
            descr.setValue("<table>"
                    + "<tr><td>Mutationsnummer:</td><td>"+message.getMutationsnummerNummer()+" / "+message.getMutationsnummerNBIdent()+"</td></tr>"
                    + "<tr><td>Beschrieb:</td><td>"+message.getBeschrieb()+"</td></tr>"
                    + "<tr><td>Mutationsplan:</td><td><a href='"+awsBaseUrl + awsBucketNameAv2Gb + "/" + message.getDateinameplan()+"'>PDF</a></td></tr>"
                    + "<tr><td>Mutationstabelle:</td><td><a href='"+awsBaseUrl + awsBucketNameAv2Gb + "/" + message.getDateinameplan().substring(0, message.getDateinameplan().length()-4)+".xml'>XML</a></td></tr>"
                    + "<tr><td>Technischer Abschluss:</td><td>"+message.getEndetechnbereit()+"</td></tr>"
                    + "<tr><td>Projektmutation:</td><td>"+message.getIstprojektmutation()+"</td></tr>"
                    + "</table>");
            item.setDescription(descr);
            item.setPubDate(message.getImportdate());

            items.add(item);
        }
 
        channel.setItems(items);
        return channel;
    }

    @RequestMapping(value="/av2gb/atom.xml", method=RequestMethod.GET)
    public Feed atom() throws Exception {
        List<Av2GbMessage> messages = av2GbMessageDAO.getAv2GbMessages();
         
        Feed feed = new Feed();
        feed.setFeedType("atom_1.0");
        feed.setTitle("AV2GB Atom Feed");
        feed.setId("https://geo.so.ch/avgbs/av2gb");
 
        Content subtitle = new Content();
        subtitle.setType("text/plain");
        subtitle.setValue("Lieferungen: Amtliche Vermessung an das Grundbuch");
        feed.setSubtitle(subtitle);
 
        Date lastDate = messages.get(0).getImportdate();
        feed.setUpdated(lastDate);
 
        List<Entry> entries = new ArrayList<Entry>();
        for (Av2GbMessage message : messages) {
            Entry entry = new Entry();

            Link link = new Link();
            link.setHref(awsBaseUrl + awsBucketNameAv2Gb + "/" + message.getDateinameplan());
            entry.setAlternateLinks(Collections.singletonList(link));
                        
            entry.setCreated(message.getImportdate());
            entry.setPublished(message.getImportdate());
            entry.setUpdated(message.getImportdate());
            entry.setId(message.getMutationsnummerNummer() + "_" + message.getMutationsnummerNBIdent());
            entry.setTitle(message.getMutationsnummerNummer() + " / " + message.getMutationsnummerNBIdent());

            Content summary = new Content();
            summary.setType("text/html");
            summary.setValue("<table>"
                    + "<tr><td>Mutationsnummer:</td><td>"+message.getMutationsnummerNummer()+" / "+message.getMutationsnummerNBIdent()+"</td></tr>"
                    + "<tr><td>Beschrieb:</td><td>"+message.getBeschrieb()+"</td></tr>"
                    + "<tr><td>Mutationsplan:</td><td><a href='"+awsBaseUrl + awsBucketNameAv2Gb + "/" + message.getDateinameplan()+"'>PDF</a></td></tr>"
                    + "<tr><td>Mutationstabelle:</td><td><a href='"+awsBaseUrl + awsBucketNameAv2Gb + "/" + message.getDateinameplan().substring(0, message.getDateinameplan().length()-4)+".xml'>XML</a></td></tr>"
                    + "<tr><td>Technischer Abschluss:</td><td>"+message.getEndetechnbereit()+"</td></tr>"
                    + "<tr><td>Projektmutation:</td><td>"+message.getIstprojektmutation()+"</td></tr>"
                    + "</table>");
            entry.setSummary(summary);

            entries.add(entry);
        }
        feed.setEntries(entries);
        return feed;
    }
}
