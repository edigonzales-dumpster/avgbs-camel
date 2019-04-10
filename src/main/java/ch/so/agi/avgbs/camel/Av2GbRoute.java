package ch.so.agi.avgbs.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    
    @Value("${app.awsAccessKey}")
    private String awsAccessKey;

    @Value("${app.awsSecretKey}")
    private String awsSecretKey;
    
    @Value("${app.awsBucketNameAv2gb}")
    private String awsBucketNameAv2gb;

    @Override
    public void configure() throws Exception {
        from("ftp://"+ftpUserInfogrips+"@"+ftpUrlInfogrips+"/\\av2gb\\?password="+ftpPwdInfogrips+"&antInclude=*.zip&autoCreate=false&noop=true&readLock=changed&stepwise=false&separator=Windows&passiveMode=true&binary=true&delay=5000&initialDelay=5000&idempotentRepository=#fileConsumerRepo&idempotentKey=av2gb-ftp-${file:name}-${file:size}-${file:modified}")
        .to("file://"+pathToAv2GbDownloadFolder)
        .split(new ZipSplitter())
        .streaming().convertBodyTo(String.class, "UTF-8")
            .choice()
                .when(body().isNotNull())
                    .to("file://"+pathToAv2GbUnzipFolder+"?charset=UTF-8") 
            .end()
        .end();        

      from("file://"+pathToAv2GbUnzipFolder+"/?noop=true&delay=30000&initialDelay=5000&readLock=changed&idempotentRepository=#fileConsumerRepo&idempotentKey=av2gb-s3-${file:name}-${file:size}-${file:modified}")
      .convertBodyTo(byte[].class)
      .setHeader(S3Constants.CONTENT_LENGTH, simple("${in.header.CamelFileLength}"))
      .setHeader(S3Constants.KEY,simple("${in.header.CamelFileNameOnly}"))
      .to("aws-s3://" + awsBucketNameAv2gb
              + "?deleteAfterWrite=false&region=EU_CENTRAL_1" //https://docs.aws.amazon.com/de_de/general/latest/gr/rande.html https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/regions/Regions.html
              + "&accessKey={{awsAccessKey}}"
              + "&secretKey=RAW({{awsSecretKey}})")
      .log(LoggingLevel.INFO, "File uploaded: ${in.header.CamelFileNameOnly}");

    }

}
