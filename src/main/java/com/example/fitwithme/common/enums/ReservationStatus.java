package com.example.fitwithme.common.enums;

public enum ReservationStatus {
    SUCCESS("예약 성공"),
    FAILURE("예약 실패"),
    OVER_CAPACITY("정원 초과");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return this.status;
    }

}