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

    @GetMapping("/detail")
    public ResponseEntity<Lesson> findLessonDetail(@RequestBody LessonRequest.detail request) {
        Lesson lessonData = subscriptionService.findLessonDetail(request);
        return ResponseEntity.ok(lessonData);
    }

    @PostMapping("/reserve")
    public ResponseEntity<LessonResponse.reserve> reserve(@Valid @RequestBody LessonRequest.reserve request, @RequestHeader("ACCESS_TOKEN") String accessToken){
        String userId = jwtUtil.getUserIdFromToken(accessToken);
        request.setUserId(userId);

        LessonResponse.reserve reserve = subscriptionService.reserve(request);
        return ResponseEntity.ok(reserve);
    }

    @PutMapping("/cancel/{reserveId}")
    public ResponseEntity<Integer> cancel(@PathVariable int reserveId){
        boolean isCancelled = subscriptionService.cancel(reserveId);
        if (isCancelled) {
            return ResponseEntity.ok(reserveId);
        } else {
            throw new BadRequestException(ErrorStatus.CANCEL_FAIL);
        }
    }

    @GetMapping
    public ResponseEntity<List<Reserve>> findReserveLessons(@RequestHeader("ACCESS_TOKEN") String accessToken) {
        String today = DateUtil.getToday();
        String userId = jwtUtil.getUserIdFromToken(accessToken);

        LessonRequest.reserveList reserveList = new LessonRequest.reserveList(today, userId);

        List<Reserve> lessonList = subscriptionService.findReserveLessons(reserveList);
        return ResponseEntity.ok(lessonList);
    }

}