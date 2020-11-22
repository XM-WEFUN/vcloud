package com.bootvue.admin.controller;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Demo {
    private List<String> tt;
    private LocalDateTime time;
}
