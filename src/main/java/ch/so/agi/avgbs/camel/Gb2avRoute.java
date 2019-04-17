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

@Component
public class Gb2avRoute extends RouteBuilder {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.ftpUserInfogrips}")
    private String ftpUserInfogrips;

    @Value("${app.ftpPwdInfogrips}")
    private String ftpPwdInfogrips;

    @Value("${app.ftpUrlInfogrips}")
    private String ftpUrlInfogrips;

    @Value("${app.pathToGb2AvDownloadFolder}")
    private String pathToGb2AvDownloadFolder;

    @Value("${app.pathToGb2AvUnzipFolder}")
    private String pathToGb2AvUnzipFolder;
    
    @Value("${app.awsAccessKey}")
    private String awsAccessKey;

    @Value("${app.awsSecretKey}")
    private String awsSecretKey;
    
    @Value("${app.awsBucketNameGb2Av}")
    private String awsBucketNameGb2Av;

    @Override
    public void configure() throws Exception {
        from("ftp://"+ftpUserInfogrips+"@"+ftpUrlInfogrips+"/\\gb2av\\?password="+ftpPwdInfogrips+"&antInclude=VOLLZUG*.zip&autoCreate=false&noop=true&readLock=changed&stepwise=false&separator=Windows&passiveMode=true&binary=true&delay=300000&initialDelay=10000&idempotentRepository=#fileConsumerRepo&idempotentKey=gb2av-ftp-${file:name}-${file:size}-${file:modified}")
        .to("file://"+pathToGb2AvDownloadFolder)
        .split(new ZipSplitter())
        .streaming().convertBodyTo(ByteBuffer.class)
            .choice()
                .when(body().isNotNull())
                    .to("file://"+pathToGb2AvUnzipFolder) 
            .end()
        .end();        

      from("file://"+pathToGb2AvUnzipFolder+"/?noop=true&delay=30000&initialDelay=5000&readLock=changed&idempotentRepository=#fileConsumerRepo&idempotentKey=gb2av-s3-${file:name}-${file:size}-${file:modified}")
      .convertBodyTo(byte[].class)
      .setHeader(S3Constants.CONTENT_LENGTH, simple("${in.header.CamelFileLength}"))
      .setHeader(S3Constants.KEY,simple("${in.header.CamelFileNameOnly}"))
      .to("aws-s3://" + awsBucketNameGb2Av
              + "?deleteAfterWrite=false&region=EU_CENTRAL_1" 
              + "&accessKey={{awsAccessKey}}"
              + "&secretKey=RAW({{awsSecretKey}})")
      .log(LoggingLevel.INFO, "File uploaded: ${in.header.CamelFileNameOnly}");

    }

}
