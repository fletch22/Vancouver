package com.fletch22.orb.command;

import com.fletch22.command.dto.Dto;

public interface JsonCommand {

	public Dto fromJson(String action);
}
