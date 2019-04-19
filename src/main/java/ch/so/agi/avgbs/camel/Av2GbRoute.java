package ch.so.agi.avgbs.camel;

import java.nio.ByteBuffer;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.so.agi.camel.predicates.IlivalidatorPredicate;

@Component
public class Av2GbRoute extends RouteBuilder {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.ftpUserInfogrips}")
    private String ftpUserInfogrips;

    @Value("${app.ftpPwdInfogrips}")
    private String ftpPwdInfogrips;

    @Value("${app.ftpUrlInfogrips}")
    private String ftpUrlInfogrips;

    @Value("${app.pathToAv2GbDownloadFolder}")
    private String pathToAv2GbDownloadFolder;

    @Value("${app.pathToAv2GbUnzipFolder}")
    private String pathToAv2GbUnzipFolder;
    
    @Value("${app.pathToAv2GbErrorFolder}")
    private String pathToAv2GbErrorFolder;
    
    @Value("${app.awsAccessKey}")
    private String awsAccessKey;

    @Value("${app.awsSecretKey}")
    private String awsSecretKey;
    
    @Value("${app.awsBucketNameAv2Gb}")
    private String awsBucketNameAv2Gb;
    
    @Value("${app.dbHostEdit}")
    private String dbHostEdit;
    
    @Value("${app.dbDatabaseEdit}")
    private String dbDatabaseEdit;
    
    @Value("${app.dbSchemaEdit}")
    private String dbSchemaEdit;

    @Value("${app.dbUserEdit}")
    private String dbUserEdit;

    @Value("${app.dbPwdEdit}")
    private String dbPwdEdit;

    @Override
    public void configure() throws Exception {
        // Download file from Infogrips ftp server every 5 seconds.
//        from("ftp://"+ftpUserInfogrips+"@"+ftpUrlInfogrips+"/\\av2gb\\?password="+ftpPwdInfogrips+"&antInclude=*.zip&autoCreate=false&noop=true&readLock=changed&stepwise=false&separator=Windows&passiveMode=true&binary=true&delay=5000&initialDelay=5000&idempotentRepository=#fileConsumerRepo&idempotentKey=av2gb-ftp-${file:name}-${file:size}-${file:modified}")
//        .to("file://"+pathToAv2GbDownloadFolder)
//        .split(new ZipSplitter())
//        .streaming().convertBodyTo(ByteBuffer.class)
//            .choice()
//                .when(body().isNotNull())
//                    .to("file://"+pathToAv2GbUnzipFolder) 
//            .end()
//        .end();        
//
//        // Upload file to S3 every 30 seconds.
//        from("file://"+pathToAv2GbUnzipFolder+"/?noop=true&delay=30000&initialDelay=5000&readLock=changed&idempotentRepository=#fileConsumerRepo&idempotentKey=av2gb-s3-${file:name}-${file:size}-${file:modified}")
//        .convertBodyTo(byte[].class)
//        .setHeader(S3Constants.CONTENT_LENGTH, simple("${in.header.CamelFileLength}"))
//        .setHeader(S3Constants.KEY,simple("${in.header.CamelFileNameOnly}"))
//        .to("aws-s3://" + awsBucketNameAv2Gb
//                + "?deleteAfterWrite=false&region=EU_CENTRAL_1" //https://docs.aws.amazon.com/de_de/general/latest/gr/rande.html https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/regions/Regions.html
//                + "&accessKey={{awsAccessKey}}"
//                + "&secretKey=RAW({{awsSecretKey}})")
//        .log(LoggingLevel.INFO, "File uploaded: ${in.header.CamelFileNameOnly}");
        
        // Import file into database every 2 minutes.
        IlivalidatorPredicate isValid = new IlivalidatorPredicate();
        
        from("file://"+pathToAv2GbUnzipFolder+"/?noop=true&include=.*\\.xml&delay=120000&initialDelay=7000&readLock=changed&idempotentRepository=#fileConsumerRepo&idempotentKey=ili2pg-${file:name}-${file:size}-${file:modified}")
        .choice()
            .when(isValid).toD("ili2pg:import?dbhost="+dbHostEdit+"&dbport=5432&dbdatabase="+dbDatabaseEdit+"&dbschema="+dbSchemaEdit+"&dbusr="+dbUserEdit+"&dbpwd="+dbPwdEdit+"&dataset=${file:onlyname.noext}")
            .otherwise().to("file://"+pathToAv2GbErrorFolder)
        .end();
    }
}
