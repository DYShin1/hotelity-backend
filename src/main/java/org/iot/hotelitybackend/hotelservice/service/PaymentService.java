package org.iot.hotelitybackend.hotelservice.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

public interface PaymentService {

	Map<String, Object> selectPaymentLogList(
		Integer pageNum, Integer customerCodeFk, String customerName, LocalDateTime paymentDate,
		Integer paymentCancelStatus, String paymentMethod, Integer reservationCodeFk,
		Integer paymentTypeCodeFk, String paymentTypeName, String orderBy, Integer sortBy
	);
}
