package ch.so.agi.avgbs.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ch.so.agi.avgbs.models.Av2GbMessage;

@Service
public class Av2GbMessageDAOImpl implements Av2GbMessageDAO {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Av2GbMessage> getAv2GbMessages() {
        String sql = "SELECT\n" + 
                "    avmutation.t_id,\n" + 
                "    beschrieb,\n" + 
                "    dateinameplan,\n" + 
                "    TO_DATE(endetechnbereit, 'YYYY-MM-DD') AS endetechnbereit,\n" + 
                "    istprojektmutation,\n" + 
                "    mutationsnummer->>'Nummer' AS mutationsnummernummer,\n" + 
                "    mutationsnummer->>'NBIdent' AS mutationsnummernbident,\n" + 
                "    importdate\n" + 
                "FROM\n" + 
                "    agi_avgbs.mutationstabelle_avmutation AS avmutation\n" + 
                "    LEFT JOIN agi_avgbs.t_ili2db_basket AS basket\n" + 
                "    ON basket.t_id = avmutation.t_basket\n" + 
                "    LEFT JOIN agi_avgbs.t_ili2db_import AS aimport\n" + 
                "    ON aimport.dataset = basket.dataset\n" + 
                "ORDER BY\n" + 
                "    importdate DESC,\n" + 
                "    endetechnbereit DESC;";
        
        RowMapper<Av2GbMessage> rowMapper = new BeanPropertyRowMapper<Av2GbMessage>(Av2GbMessage.class);
        List<Av2GbMessage> list = jdbcTemplate.query(sql, rowMapper);
        return list;
    }

}
