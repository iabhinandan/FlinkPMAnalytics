package org.abhinandan.config;

public class FlinkStreamingConfig {
    final static String S3_END_POINT = "s3.us-west-2.amazonaws.com";
    final static String S3_BASE_PATH ="s3a://flinks3integration";


    public static String getS3EndPoint() {
        return S3_END_POINT;
    }

    public static String getS3BasePath() {
        return S3_BASE_PATH;
    }



}
