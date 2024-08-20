package com.example.fitwithme.application.service;

import com.example.fitwithme.common.enums.ReservationStatus;
import com.example.fitwithme.domain.model.Lesson;
import com.example.fitwithme.domain.model.Reserve;
import com.example.fitwithme.domain.model.Subscription;
import com.example.fitwithme.infrastructure.dao.SubscriptionDao;
import com.example.fitwithme.presentation.dto.request.LessonRequest;
import com.example.fitwithme.presentation.dto.response.LessonResponse;
import com.example.fitwithme.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {
    private final SubscriptionDao subscriptionDao;

    public List<Subscription> findSubscription(Long centerId) {
        List<Subscription> subscriptions = subscriptionDao.findSubscription(centerId);

        return subscriptions;
    }
}