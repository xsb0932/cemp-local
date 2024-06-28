import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioTest {
    public static void main(String[] args) {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://172.20.1.91:19000")
                            .credentials("sTi7yItFrvOoFUWyKZH2", "OQ21CBymqEYjPTB0XLtFvuUVUOP2FgtssHgHHStB")
                            .build();

            // Make 'asiatrip' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("cemp").build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("cemp").build());
            } else {
                System.out.println("Bucket 'cemp' already exists.");
            }
            // 文件名
            String filename = "jinjiangGIC_logo_white.png";
            // 本地文件路径
            String localFile = "/Users/eason/Downloads/jinjiangGIC_logo_white_slices/jinjiangGIC_logo_white.png";

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("cemp")
                            // 文件名
                            .object(filename)
                            // 本地文件路径
                            .filename(localFile)
                            .build());
            System.out.println(localFile + " is successfully uploaded as " + "object '" + filename + "' to bucket 'cemp'.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
