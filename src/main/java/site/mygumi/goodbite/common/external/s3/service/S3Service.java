package site.mygumi.goodbite.common.external.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mygumi.goodbite.exception.s3.S3ErrorCode;
import site.mygumi.goodbite.exception.s3.detail.S3EmptyFileException;
import site.mygumi.goodbite.exception.s3.detail.S3FileDeleteFailedException;
import site.mygumi.goodbite.exception.s3.detail.S3FileUploadFailedException;
import site.mygumi.goodbite.exception.s3.detail.S3InvalidFileExtensionException;
import site.mygumi.goodbite.exception.s3.detail.S3InvalidS3UrlException;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // S3에 저장된 이미지 객체의 public url 반환
    public String upload(MultipartFile image) {
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new S3EmptyFileException(S3ErrorCode.EMPTY_FILE);
        }
        return this.uploadImage(image);
    }

    // 이미지를 S3에 업로드, S3에 저장된 이미지의 public url 반환
    private String uploadImage(MultipartFile image) {
        String filename = Objects.requireNonNull(image.getOriginalFilename(),
            "파일 이름은 null일 수 없습니다.");
        this.validateImageFileExtension(filename);
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new S3FileUploadFailedException(S3ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // 파일 확장자 jpg, jpeg, png, gif 만 가능
    private void validateImageFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extension)) {
            throw new S3InvalidFileExtensionException(S3ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    // S3에 업로드
    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        //변경된 파일 명
        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename;

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is); //image를 byte[]로 변환

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extension);
        metadata.setContentLength(bytes.length);

        //S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            //실제로 S3에 이미지 데이터를 넣음
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new S3FileUploadFailedException(S3ErrorCode.FILE_UPLOAD_FAILED);
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    // S3에서 이미지 제거
    public void deleteImageFromS3(String imageAddress) {
        String key = getKeyFromImageAddress(imageAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new S3FileDeleteFailedException(S3ErrorCode.FILE_DELETE_FAILED);
        }
    }

    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = (new URI(imageAddress)).toURL();
            String decodingKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
            String bucketPrefix = "/" + bucketName + "/";
            if (decodingKey.startsWith(bucketPrefix)) {
                return decodingKey.substring(bucketPrefix.length());
            } else {
                throw new S3InvalidS3UrlException(S3ErrorCode.INVALID_S3_URL);
            }
        } catch (MalformedURLException | URISyntaxException e) {
            throw new S3FileDeleteFailedException(S3ErrorCode.FILE_DELETE_FAILED);
        }
    }
}