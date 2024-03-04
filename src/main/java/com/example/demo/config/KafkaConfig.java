@Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        configs.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        configs.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        configs.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfiguration);
        return new KafkaAdmin(configs);
    }

 @Bean
    public KafkaTemplate<String, ?> kafkaTemplate() {
        KafkaTemplate<String, ?> template = null;
        try {
            template = new KafkaTemplate<>(producerFactory());
        } catch (Exception e) {
            System.out.println("Error in kafka bean creation : " + e.getMessage());
        }
        return template;
    }


@Bean
    public ProducerFactory<String, Object> producerFactory() {
        final Map<String, Object> configs = new ConcurrentHashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        configs.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        configs.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        configs.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfiguration);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
//        configs.put(ProducerConfig.ACKS_CONFIG, "-1");
        //for producing to kafka topic in batches
        configs.put(ProducerConfig.LINGER_MS_CONFIG, "100");
        configs.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(1000 * 1024));
        return new DefaultKafkaProducerFactory<>(configs);
    }



   @Bean
    public ConsumerFactory<String, Bean> consumerFactory() {
        try (JsonDeserializer<Bean> deserializer = new JsonDeserializer<>(Bean.class)) {
            deserializer.setRemoveTypeHeaders(false);
            deserializer.addTrustedPackages("*");
            deserializer.setUseTypeMapperForKey(true);
            final Map<String, Object> configs = new ConcurrentHashMap<>();
            configs.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            configs.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
            configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
            configs.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfiguration);
            configs.put(ConsumerConfig.GROUP_ID_CONFIG, batchGroupId);
            configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
            configs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxMoengageRecords);
            configs.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, maxWaitTime);
            configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            configs.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, ConsumerConfig.DEFAULT_FETCH_MAX_BYTES);
            DefaultKafkaConsumerFactory<String, Bean> defaultKafkaConsumerFactory;
            try (StringDeserializer stringDeserializer = new StringDeserializer()) {
                defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(configs,
                        stringDeserializer, deserializer);
            }
            return defaultKafkaConsumerFactory;
        }
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Bean> containerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, Bean> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        return factory;
    }
