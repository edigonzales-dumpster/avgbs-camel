package ch.so.agi.avgbs.jsf;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ch.so.agi.avgbs.dao.Av2GbMessageDAOImpl;
import ch.so.agi.avgbs.models.Av2GbMessage;

@Named("av2gb")
@ViewScoped
public class Av2GbView implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Value("${app.awsBucketNameAv2Gb}")
    private String awsBucketNameAv2Gb;
    
    private String awsBaseUrl = "https://s3.eu-central-1.amazonaws.com/";

    @Autowired
    Av2GbMessageDAOImpl av2GbMessageDAO;
    
    private List<Av2GbMessage> messageList;

    public List<Av2GbMessage> getMessageList() {
        if (messageList == null) {
            messageList = av2GbMessageDAO.getAv2GbMessages();
            
            for (Av2GbMessage msg : messageList) {
                msg.setDateinameplan(awsBaseUrl + awsBucketNameAv2Gb + "/" + msg.getDateinameplan());
            }
        }

        return messageList;
    }

}
