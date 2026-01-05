package com.jpmc.midascore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="general")
public class GenralProperties
{
    private String kafkaTopic;

    public String getKafkaTopic(){
        return kafkaTopic;
    }
    public void setKafkaTopic(String kafkaTopicPassed)
    {
        this.kafkaTopic = kafkaTopicPassed;
    }
}