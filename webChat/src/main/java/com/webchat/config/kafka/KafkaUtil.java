package com.webchat.config.kafka;

import org.apache.kafka.clients.admin.*;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class KafkaUtil {

    public static void createTopic(String bootstrapServers, String topicName, int numPartitions, short replicationFactor) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(props)) {
            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
        }
    }

    public static void initTopics(String bootstrapServers, List<Integer> roomIds) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(props)) {
            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true);

            ListTopicsResult topicsResult = adminClient.listTopics(options);
            Set<String> topics = topicsResult.names().get();

            for(Integer roomId : roomIds) {
                if(!topics.contains("room"+roomId)) {
                    createTopic(bootstrapServers, "room"+roomId, 3, (short) 1);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
