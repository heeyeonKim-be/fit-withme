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
        Long reserveId = lessonDao.create(request);

        LessonResponse.reserve response = LessonResponse.reserve.builder()
                .reserveId(reserveId)
                .status(reserveId > 0 ? ReservationStatus.SUCCESS : ReservationStatus.FAILURE)
                .build();

        return response;

//        // 먼저 해당 수업 정보를 가져오면서 잠금을 겁니다.
//        Lesson lesson = lessonDao.findByIdForUpdate(request.getLessonId());
//
//        // 수업의 최대 정원과 현재 예약된 인원수를 비교합니다.
//        if (lesson.getCurrentCapacity() < lesson.getMaxCapacity()) {
//            // 예약 가능하면 예약을 생성합니다.
//            Long reserveId = lessonDao.create(request);
//
//            return LessonResponse.reserve.builder()
//                    .reserveId(reserveId)
//                    .status(ReservationStatus.SUCCESS)
//                    .build();
//        } else {
//            // 예약 불가능하면 실패 응답을 반환합니다.
//            return LessonResponse.reserve.builder()
//                    .status(ReservationStatus.FAILURE)
//                    .build();
//        }
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