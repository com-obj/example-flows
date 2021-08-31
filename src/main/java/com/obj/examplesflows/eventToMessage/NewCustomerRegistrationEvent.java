package com.obj.examplesflows.eventToMessage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.obj.nc.domain.IsTypedJson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = Id.CLASS)
public class NewCustomerRegistrationEvent implements IsTypedJson {

	private String customerName;
	private String customerEmail;
}
