package com.airtel.dto;

import java.util.List;

import lombok.Data;

@Data
public class UpdateJira {
	
	private List<SummaryJiraDTO> update;
	
	@Data
	public class SummaryJiraDTO{
		
		private String set;
		
	}
	

}
