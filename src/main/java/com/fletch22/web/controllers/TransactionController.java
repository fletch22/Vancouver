package com.fletch22.web.controllers;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController extends Controller {
	
	Logger logger = LoggerFactory.getLogger(TransactionController.class);

	@RequestMapping(value = "/{transactionId}", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String stateHistory(@PathVariable BigDecimal transactionId, @RequestParam(value="action", required=true) String action) {

		logger.info("Getting transactionId {}", transactionId);

		return JSON_SUCCESS;
	}
}
