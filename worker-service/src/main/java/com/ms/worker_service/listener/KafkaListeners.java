package com.ms.worker_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ms.worker_service.model.exceptions.ExternalApiException;
import com.ms.worker_service.repository.NewsRepository;
import com.ms.worker_service.service.MediaStackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.ms.worker_service.utils.Contants.MESSAGE_GROUP_NAME;
import static com.ms.worker_service.utils.Contants.TOPIC_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaListeners {

    private final MediaStackService mediaStackService;
    private final NewsRepository newsRepository;

    @KafkaListener(topics = TOPIC_NAME, groupId = MESSAGE_GROUP_NAME)
    public void listener(String date){
        log.info("Listener received: {}", date);
        mediaStackService.sendRequest(date)
                .flatMap(response -> {
                    try {
                        return newsRepository.saveNews(date, response.getBody());
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("Error processing JSON", e));
                    }
                })
                .doOnNext(saved -> {
                    if (saved) {
                        log.info("Data successfully cached.");
                    } else {
                        log.warn("Data was not cached.");
                    }
                })
                .doOnError(error -> {
                    if (error instanceof ExternalApiException apiException) {
                        log.error("Failure in external API - Status: {}, Body: {}", apiException.getStatus(), apiException.getResponseBody());
                    }else {
                        log.error("Error processing Kafka message: {}", error.getMessage(), error);
                    }
                })
                .subscribe();
    }
}
