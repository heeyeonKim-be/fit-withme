package com.example.fitwithme.presentation.controller;

import com.example.fitwithme.application.service.SubscriptionService;
import com.example.fitwithme.common.exception.BadRequestException;
import com.example.fitwithme.common.exception.ErrorStatus;
import com.example.fitwithme.domain.model.Lesson;
import com.example.fitwithme.domain.model.Reserve;
import com.example.fitwithme.domain.model.Subscription;
import com.example.fitwithme.jwt.JwtUtil;
import com.example.fitwithme.presentation.dto.request.LessonRequest;
import com.example.fitwithme.presentation.dto.response.LessonResponse;
import com.example.fitwithme.util.DateUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final JwtUtil jwtUtil;

    private final SubscriptionService subscriptionService;

    @GetMapping("/{centerId}")
    public ResponseEntity<List<Subscription>> findSubscription(@PathVariable Long centerId) {

        List<Subscription> subscriptionList = subscriptionService.findSubscription(centerId);
        return ResponseEntity.ok(subscriptionList);
    }

}