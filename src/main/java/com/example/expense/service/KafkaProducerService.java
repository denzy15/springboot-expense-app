package com.example.expense.service;

import com.example.expense.DTO.kafka.BudgetAccessEventDTO;
import com.example.expense.DTO.kafka.CategoryEventDTO;
import com.example.expense.DTO.kafka.TransactionEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, TransactionEventDTO> transactionKafkaTemplate;
    private final KafkaTemplate<String, CategoryEventDTO> categoryKafkaTemplate;
    private final KafkaTemplate<String, BudgetAccessEventDTO> budgetAccessKafkaTemplate;

    @Value("${app.kafka.topic.transaction.create}")
    private String transactionCreateTopic;

    @Value("${app.kafka.topic.transaction.update}")
    private String transactionUpdateTopic;

    @Value("${app.kafka.topic.transaction.delete}")
    private String transactionDeleteTopic;

    @Value("${app.kafka.topic.category.create}")
    private String categoryCreateTopic;

    @Value("${app.kafka.topic.category.update}")
    private String categoryUpdateTopic;

    @Value("${app.kafka.topic.category.delete}")
    private String categoryDeleteTopic;

    @Value("${app.kafka.topic.budgetMember.create}")
    private String budgetMemberCreateTopic;

    @Value("${app.kafka.topic.budgetMember.update}")
    private String budgetMemberUpdateTopic;

    @Value("${app.kafka.topic.budgetMember.delete}")
    private String budgetMemberDeleteTopic;

    public void sendTransactionCreateEvent(TransactionEventDTO event) {
        transactionKafkaTemplate.send(transactionCreateTopic, event);
    }
}
