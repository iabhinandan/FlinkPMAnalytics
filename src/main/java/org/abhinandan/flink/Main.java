package org.abhinandan.flink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class Main {
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // Make sure you are running with one thread locally

        System.out.println(System.getenv("AWS_ACCESS_KEY"));
        System.out.println(System.getenv("AWS_SECRET_KEY"));

        Configuration config = new Configuration();
        config.setString("fs.s3a.access.key", "");
        config.setString("fs.s3a.secret.key", "");
        config.setString("fs.s3a.endpoint", "s3.us-west-2.amazonaws.com"); // Use HTTP, not HTTPS
        config.setString("fs.s3a.path.style.access", "true");



        env.configure(config);

        // Create a FileSystem object
        FileSystem fs = FileSystem.get(new Path("s3://flinks3integration/completed-jobs/sample/Azure/").toUri());

        // Listing files in a given bucket
        String directoryPath = "s3://flinks3integration/completed-jobs/sample/Azure/";  // Change this to your bucket path
        Path path = new Path(directoryPath);
        org.apache.flink.core.fs.FileStatus[] fileStatuses = fs.listStatus(path);

        for (org.apache.flink.core.fs.FileStatus status : fileStatuses) {
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