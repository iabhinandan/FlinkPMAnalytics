package org.abhinandan.config;

public class FlinkStreamingConfig {
    final static String S3_END_POINT = "s3.us-west-2.amazonaws.com";
    final static String S3_BASE_PATH ="s3a://flinks3integration";
    final static String INFLUX_DB_TOKEN = "TDYLoMF9jo_eo5VOsFcbZwqf_Dz-GMZP44ayzI0WYxjXVksRcAlJO_VVb5-lJRzLswDc0b-n_MpIgkNGoF6Xhw==";


    public static String getS3EndPoint() {
        return S3_END_POINT;
    }

    public static String getS3BasePath() {
        return S3_BASE_PATH;
    }

    public static String getInfluxDbToken(){
        return INFLUX_DB_TOKEN;
    }



}
