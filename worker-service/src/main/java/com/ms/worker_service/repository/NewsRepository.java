package com.ms.worker_service.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

public interface NewsRepository {

    Mono<Boolean> saveNews(String date, Object newsObject) throws JsonProcessingException;
}
