package ch.so.agi.avgbs.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ch.so.agi.avgbs.dao.Av2GbMessageDAOImpl;
import ch.so.agi.avgbs.models.Av2GbMessage;

@Controller
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Av2GbMessageDAOImpl av2GbMessageDAO;

    @RequestMapping(value="/av2gb/rss", method=RequestMethod.GET, produces={MediaType.ALL_VALUE})
    public ResponseEntity<?> getAv2GbMessages () throws Exception {
        
        List<Av2GbMessage> messages = av2GbMessageDAO.getAv2GbMessages();
        log.info(messages.get(0).getBeschrieb());
        log.info(messages.get(0).getMutationsnummerNBIdent());
        
        return ResponseEntity.ok("fubar");

    }

}
