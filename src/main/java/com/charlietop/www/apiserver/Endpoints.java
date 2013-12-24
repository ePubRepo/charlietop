package com.charlietop.www.apiserver;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import javax.inject.Named;

@Api(name = "apiserver", version = "v1")
public class Endpoints {
	@ApiMethod(name = "apiserver.addstoredvalue", httpMethod = "get")
	public CardBalance addStoredValue(@Named("cardnum") String cardNum) {
		return new CardBalance(15.50);
	}
}
