package com.server.server.module.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyMinutesStatVO {

    private LocalDate date;
    private Integer minutes;
}
