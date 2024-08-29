package com.example.fitwithme.application.service;

import com.example.fitwithme.common.enums.ReservationStatus;
import com.example.fitwithme.common.exception.BadRequestException;
import com.example.fitwithme.common.exception.ErrorStatus;
import com.example.fitwithme.domain.model.Lesson;
import com.example.fitwithme.domain.model.Reserve;
import com.example.fitwithme.domain.model.User;
import com.example.fitwithme.infrastructure.dao.LessonDao;
import com.example.fitwithme.infrastructure.dao.UserDao;
import com.example.fitwithme.jwt.JwtUtil;
import com.example.fitwithme.presentation.dto.request.LessonRequest;
import com.example.fitwithme.presentation.dto.request.UserRequest;
import com.example.fitwithme.presentation.dto.response.LessonResponse;
import com.example.fitwithme.presentation.dto.response.UserResponse;
import com.example.fitwithme.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class LessonService {
    private final LessonDao lessonDao;

    public List<Lesson> findLessons(String selectDate) {
        String day = DateUtil.getDayByDate(selectDate);

        return lessonDao.findAllLesson(selectDate, day);
    }

    public Lesson findLessonDetail(LessonRequest.detail request) {

        Long lessonId = request.getLessonId();

        Lesson lessonBase = lessonDao.findLessonById(lessonId);
        int currentPersonnel = lessonDao.countCurrentPersonnel(request);

        return Lesson.builder()
                .lessonId(lessonBase.lessonId())
                .center(lessonBase.center())
                .lessonName(lessonBase.lessonName())
                .instructorName(lessonBase.instructorName())
                .currentPersonnel(currentPersonnel)
                .personnel(lessonBase.personnel())
                .lessonDay(lessonBase.lessonDay())
                .startTime(lessonBase.startTime())
                .endTime(lessonBase.endTime())
                .build();
    }

    @Transactional
    public LessonResponse.reserve reserve(LessonRequest.reserve request) {
//        Long reserveId = lessonDao.create(request);
//
//        LessonResponse.reserve response = LessonResponse.reserve.builder()
//                .reserveId(reserveId)
//                .status(reserveId > 0 ? ReservationStatus.SUCCESS : ReservationStatus.FAILURE)
//                .build();
//
//        return response;

        // 1. 해당 수업에 대해 잠금을 걸고 현재 예약 인원 가져오기
        int currentReservations = lessonDao.getReservationCountForUpdate(request.getLessonId(), request.getSelectDate());

        // 2. 수업 정보 가져오기 (정원 확인)
        Lesson lesson = lessonDao.findLessonById(request.getLessonId());

        // 3. 정원 확인 후 예약 생성
        if (currentReservations < lesson.personnel()) {
            Long reserveId = lessonDao.create(request);

            return LessonResponse.reserve.builder()
                    .reserveId(reserveId)
                    .status(ReservationStatus.SUCCESS)
                    .build();
        } else {
            // 정원이 초과된 경우
            return LessonResponse.reserve.builder()
                    .status(ReservationStatus.OVER_CAPACITY)
                    .build();
        }

    }

    @Transactional
    public boolean cancel(int reserveId) {
        int result = lessonDao.deleteReserve(reserveId);

        if(result > 0){
            return true;
        }else {
            return false;
        }
    }

    public List<Reserve> findReserveLessons(LessonRequest.reserveList reserveList) {
        List<Reserve> reserves = lessonDao.findAllReserveByUserIdAndDate(reserveList);
        List<Reserve> completeReserves = new ArrayList<>();

        for (Reserve reserve : reserves) {
            Lesson lessonDetails = lessonDao.findLessonDetailsByLessonId(reserve.lessonId());

            Reserve completeReserve = new Reserve(
                    reserve.reserveId(),
                    reserve.userId(),
                    reserve.reserveDate(),
                    reserve.lessonId(),
                    lessonDetails.lessonName(),
                    lessonDetails.instructorName(),
                    reserve.currentPersonnel(),
                    lessonDetails.personnel(),
                    lessonDetails.lessonDay(),
                    lessonDetails.startTime(),
                    lessonDetails.endTime()
            );
            completeReserves.add(completeReserve);
        }

        return reserves;
    }
}