package site.mygumi.goodbite.common.external.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final Dotenv dotenv;

    @Bean
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
            Objects.requireNonNull(dotenv.get("S3_ACCESS_KEY")),
            Objects.requireNonNull(dotenv.get("S3_SECRET_KEY"))
        );
        return AmazonS3ClientBuilder.standard()
            .withRegion(dotenv.get("S3_REGION"))
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();
    }

    @Bean
    public String s3BucketName() {
        return dotenv.get("S3_BUCKET_NAME");
    }

}