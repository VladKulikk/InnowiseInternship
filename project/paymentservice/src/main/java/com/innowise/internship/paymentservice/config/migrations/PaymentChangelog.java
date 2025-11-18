package com.innowise.internship.paymentservice.config.migrations;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "payment-changelog-v1", order = "001", author = "admin")
public class PaymentChangelog {

    public static final String COLLECTION_NAME = "payments";

    @Execution
    public void createPaymentIndexes(MongoTemplate mongoTemplate) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(COLLECTION_NAME);

        collection.createIndex(new Document("order_id", 1), new IndexOptions().name("idx_order_id"));

        collection.createIndex(new Document("user_id", 1), new IndexOptions().name("idx_user_id"));

        collection.createIndex(new Document("status", 1), new IndexOptions().name("idx_status"));
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(COLLECTION_NAME);

        collection.dropIndex("idx_order_id");
        collection.dropIndex("idx_user_id");
        collection.dropIndex("idx_status");
    }
}
