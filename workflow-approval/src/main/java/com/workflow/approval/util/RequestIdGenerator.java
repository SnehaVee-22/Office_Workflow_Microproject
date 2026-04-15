package com.workflow.approval.util;

import com.workflow.approval.repository.RequestRepository;
import org.springframework.stereotype.Component;

@Component
public class RequestIdGenerator {

    private final RequestRepository requestRepository;

    public RequestIdGenerator(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public String generate() {
        long count = requestRepository.count() + 1;
        return String.format("REQ%04d", count);
    }
}
