package com.example.fitwithme.infrastructure.dao;

import com.example.fitwithme.common.exception.BadRequestException;
import com.example.fitwithme.common.exception.ErrorStatus;
import com.example.fitwithme.domain.model.Lesson;
import com.example.fitwithme.domain.model.Reserve;
import com.example.fitwithme.infrastructure.mapper.SubscriptionMapper;
import com.example.fitwithme.presentation.dto.request.LessonRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscriptionDao {
    private final SubscriptionMapper subscriptionMapper;

    public List<Lesson> findAllLesson(String selectDate, String day) {
        try {
            return subscriptionMapper.findAllLesson(selectDate, day);
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorStatus.NOT_FOUND_LESSONLIST);
        }
    }

    public Lesson findLessonById(Long lessonId) {
        try {
            return subscriptionMapper.findLessonById(lessonId);
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorStatus.NOT_FOUND_LESSON);
        }
    }

    public int countCurrentPersonnel(LessonRequest.detail request) {
        try {
            return subscriptionMapper.countCurrentPersonnel(request);
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorStatus.NOT_FOUND_LESSON);
        }
    }

    public Long create(LessonRequest.reserve request) {
        Long result = subscriptionMapper.create(request);

        return result;
    }

    public int deleteReserve(int reserveId) {
        return subscriptionMapper.cancel(reserveId);
    }

    public List<Reserve> findAllReserveByUserIdAndDate(LessonRequest.reserveList reserveList) {
        try {
            return subscriptionMapper.findAllReserveByUserIdAndDate(reserveList);
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorStatus.NOT_FOUND_RESERVEIST);
        }
    }

    public Lesson findLessonDetailsByLessonId(Long lessonId) {
        try {
            return subscriptionMapper.findLessonDetailsByLessonId(lessonId);
        } catch (EmptyResultDataAccessException e) {
            throw new BadRequestException(ErrorStatus.NOT_FOUND_RESERVEIST);
        }
    }

}
