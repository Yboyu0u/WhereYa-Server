package com.where.WhereYouAt.controller.dto.appointment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AppointmentListResponseDto {

    private List<AppointmentResponseDto> promiseList;
}
