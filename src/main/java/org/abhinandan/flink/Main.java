package org.abhinandan.flink;

import org.abhinandan.config.FlinkStreamingConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class Main {
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // Make sure you are running with one thread locally

        Configuration config = new Configuration();
        config.setString("fs.s3a.access.key", System.getenv("AWS_ACCESS_KEY"));
        config.setString("fs.s3a.secret.key", System.getenv("AWS_SECRET_KEY"));
        config.setString("fs.s3a.endpoint", FlinkStreamingConfig.getS3EndPoint()); // Use HTTP, not HTTPS
        config.setString("fs.s3a.path.style.access", "true");

        env.configure(config);

        // Create a FileSystem object
        FileSystem fs = FileSystem.get(new Path(FlinkStreamingConfig.getS3BasePath()+"/completed-jobs/sample/Azure/").toUri());

        // Listing files in a given bucket
        String directoryPath = FlinkStreamingConfig.getS3BasePath()+"/completed-jobs/sample/Azure/";  // Change this to your bucket path
        Path path = new Path(directoryPath);
        FileStatus[] fileStatuses = fs.listStatus(path);

        for (FileStatus status : fileStatuses) {
            System.out.println(status.getPath());
        }

        // Your Flink job
        env.fromElements("Hello", "Flink", "World")
                .map(value -> value + " processed")
                .print();  // Print the transformed elements



        // Execute the job
        env.execute("Flink Local Job");
    }
}