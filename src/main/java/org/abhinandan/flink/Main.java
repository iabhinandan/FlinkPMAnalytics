package org.abhinandan.flink;

import org.abhinandan.config.FlinkStreamingConfig;
import org.abhinandan.pojo.PMKafkaEventPojo;
import org.abhinandan.sampledata.TelecomPMData;
import org.abhinandan.util.FlinkJsonToPojo;
import org.abhinandan.util.FlinkJsonToRowMapper;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.types.Row;


public class Main {
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // Make sure you are running with one thread locally

        System.out.println(System.getenv("AWS_ACCESS_KEY"));
        System.out.println(System.getenv("AWS_SECRET_KEY"));

        Configuration config = new Configuration();
        config.setString("fs.s3a.access.key", System.getenv("AWS_ACCESS_KEY"));
        config.setString("fs.s3a.secret.key", System.getenv("AWS_SECRET_KEY"));
        config.setString("fs.s3a.endpoint", FlinkStreamingConfig.getS3EndPoint()); // Use HTTP, not HTTPS
        config.setString("fs.s3a.path.style.access", "true");

        env.configure(config);

        DataStream<String> jsonStream = env.fromElements(TelecomPMData.getJsonString());

        // 3️⃣ Convert JSON to POJO
        DataStream<PMKafkaEventPojo> pojoStream = jsonStream.map(new FlinkJsonToPojo());

        DataStream<Row> rowStream = pojoStream.map(new FlinkJsonToRowMapper());


        //rowStream.print();

        String s3PMPath = FlinkStreamingConfig.getS3BasePath()+"/pm-output-path"; // Replace with your S3 bucket path


        rowStream
                .sinkTo(FileSink
                        .forRowFormat(new Path(s3PMPath), new SimpleStringEncoder<Row>("UTF-8"))
                        .withBucketCheckInterval(1000)
                        .withRollingPolicy(DefaultRollingPolicy.builder().build())
                        .build());

        /*
        // Create a FileSystem object
        FileSystem fs = FileSystem.get(new Path(FlinkStreamingConfig.getS3BasePath()+"/completed-jobs/sample/Azure/").toUri());

        // Listing files in a given bucket
        String directoryPath = FlinkStreamingConfig.getS3BasePath()+"/completed-jobs/sample/Azure/";  // Change this to your bucket path
        Path path = new Path(directoryPath);
        FileStatus[] fileStatuses = fs.listStatus(path);

        for (FileStatus status : fileStatuses) {
            System.out.println(status.getPath());
        }
*/
        // Your Flink job
        env.fromElements("Hello", "Flink", "World")
                .map(value -> value + " processed")
                .print();  // Print the transformed elements



        // Execute the job
        env.execute("Flink Local Job");
    }
}