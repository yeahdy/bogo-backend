package com.boardgo.domain.chatting.repository;

import com.boardgo.domain.chatting.entity.ChatMessage;
import com.boardgo.domain.chatting.repository.projection.LatestMessageProjection;
import java.util.List;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<ChatMessage, String> {
    @Aggregation(
            pipeline = {
                "{ '$match': { 'roomId': { '$in': ?0 } } }",
                "{ '$sort': { 'sendDatetime': -1 } }",
                "{ '$group': { '_id': '$roomId', 'latestMessage': { '$first': '$$ROOT' } } }"
            })
    List<LatestMessageProjection> findLatestMessagesByRoomIds(List<Long> roomIds);

    List<ChatMessage> findByRoomId(Long roomId);
}
