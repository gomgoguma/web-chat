package com.webchat.config.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;

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

    public static boolean checkTopicExist(String bootstrapServers, String topicName) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(properties)) {
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singleton(topicName), new DescribeTopicsOptions().timeoutMs(5000));

            KafkaFuture<TopicDescription> topicDescriptionFuture = describeTopicsResult.topicNameValues().get(topicName);
            try {
                TopicDescription topicDescription = topicDescriptionFuture.get();
                return topicDescription != null;
            } catch (InterruptedException | ExecutionException e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteTopic( String bootstrapServers, String topic) {
        if(checkTopicExist(bootstrapServers, topic))  {
            Properties properties = new Properties();
            properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

            // AdminClient 생성
            try (AdminClient adminClient = AdminClient.create(properties)) {
                // 삭제할 토픽 이름 설정

                // 토픽 삭제 요청 생성
                DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Collections.singleton(topic));

                // 삭제 결과 확인
                try {
                    deleteTopicsResult.all().get();
                } catch (InterruptedException | ExecutionException e) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
