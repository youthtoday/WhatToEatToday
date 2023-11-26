package com.whattoeattoday.recommendationservice.recommendation;

import com.whattoeattoday.recommendationservice.common.BaseResponse;
import com.whattoeattoday.recommendationservice.common.Status;
import com.whattoeattoday.recommendationservice.recommendation.request.GetVectorizedSimilarityRankOnMultiFieldRequest;
import com.whattoeattoday.recommendationservice.recommendation.request.GetVectorizedSimilarityRankRequest;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.dataproc.v1.Job;
import com.google.cloud.dataproc.v1.JobControllerClient;
import com.google.cloud.dataproc.v1.JobControllerSettings;
import com.google.cloud.dataproc.v1.JobMetadata;
import com.google.cloud.dataproc.v1.JobPlacement;
import com.google.cloud.dataproc.v1.SparkJob;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

/**
 * @author Lijie Huang lh3158@columbia.edu
 * @date 10/19/23
 */
@ConfigurationProperties(prefix = "spring")
@Service
public class VectorizedSimilarityServiceImpl implements VectorizedSimilarityService{

    private String projectId = "whattoeattoday-401920";
    private String region = "us-central1";
    private String clusterName = "cluster-9428";

    @Override
    public BaseResponse<List<Row>> getVectorizedSimilarityRank(GetVectorizedSimilarityRankRequest request) {
        // TODO Param Validation
        return null;
    }

    @Override
    public BaseResponse<List<Integer>> getVectorizedSimilarityRankOnMultiField(GetVectorizedSimilarityRankOnMultiFieldRequest request) throws IOException, ExecutionException, InterruptedException {
        // TODO Param Check
        // TODO Double Type Unsupported
        String tableName = request.getCategoryName();
        List<String> fieldNameArr = request.getFieldNameList();
        StringBuilder fieldNameSb = new StringBuilder();
        for (String fieldName : fieldNameArr) {
            fieldNameSb.append(fieldName);
            fieldNameSb.append(",");
        }
        fieldNameSb.deleteCharAt(fieldNameSb.length()-1);

        String myEndpoint = String.format("%s-dataproc.googleapis.com:443", region);

        // Configure the settings for the job controller client.
        JobControllerSettings jobControllerSettings =
                JobControllerSettings.newBuilder().setEndpoint(myEndpoint).build();

        // Create a job controller client with the configured settings. Using a try-with-resources
        // closes the client,
        // but this can also be done manually with the .close() method.
        try (JobControllerClient jobControllerClient =
                     JobControllerClient.create(jobControllerSettings)) {

            // Configure cluster placement for the job.
            JobPlacement jobPlacement = JobPlacement.newBuilder().setClusterName(clusterName).build();

            String[] argsArr = new String[]{tableName, fieldNameSb.toString(), request.getTargetId(), String.valueOf(request.getRankTopSize()), "result"};
            List<String> args = Arrays.asList(argsArr);
            // Configure Spark job settings.
            SparkJob sparkJob =
                    SparkJob.newBuilder()
                            .setMainClass("com.whattoeattoday.RecommendOnItem")
                            .addJarFileUris("gs://4156-recommend-job/Recommend-1.0-SNAPSHOT.jar")
                            .addAllArgs(args)
                            .build();

            Job job = Job.newBuilder().setPlacement(jobPlacement).setSparkJob(sparkJob).build();

            // Submit an asynchronous request to execute the job.
            OperationFuture<Job, JobMetadata> submitJobAsOperationAsyncRequest =
                    jobControllerClient.submitJobAsOperationAsync(projectId, region, job);

            Job response = submitJobAsOperationAsyncRequest.get();

            System.out.println("response.getDriverOutputResourceUri(): "+ response.getDriverOutputResourceUri());

            // Print output from Google Cloud Storage.
            Matcher matches =
                    Pattern.compile("gs://(.*?)/(.*)").matcher(response.getDriverOutputResourceUri());
            matches.matches();

            Storage storage = StorageOptions.getDefaultInstance().getService();
            Blob blob = storage.get(matches.group(1), String.format("%s.000000000", matches.group(2)));

            System.out.println(
                    String.format("Job finished successfully: %s", new String(blob.getContent())));
        }
        return null;
//        return BaseResponse.with(Status.SUCCESS, resultList);
    }
}
