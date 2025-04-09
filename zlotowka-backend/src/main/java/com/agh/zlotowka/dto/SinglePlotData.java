package com.agh.zlotowka.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


public record SinglePlotData(LocalDate date, BigDecimal amount) {}
