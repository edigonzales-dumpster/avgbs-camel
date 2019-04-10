/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.so.agi.avgbs;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author stefan
 */
@Controller
public class MainController {
    @RequestMapping(value="/rss", method=RequestMethod.GET, produces={MediaType.ALL_VALUE})
    public ResponseEntity<?> getXmlExtractById () throws Exception {
        
        System.out.println("gagaaaaa");
        System.out.println("adfadf");
        System.out.println("adfadf");
        System.out.println("aaaa");
        System.out.println("aaafff");
                
        
        
        return ResponseEntity.ok("fubar");
    }

}
