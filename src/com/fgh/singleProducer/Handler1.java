package com.fgh.singleProducer;

import com.fgh.genrate1.Trade;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class Handler1 implements EventHandler<Trade>,WorkHandler<Trade> {

	@Override
	public void onEvent(Trade event) throws Exception {
		onEvent(event);
	}

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("handler1 set name:");
		event.setName("h1");
	}

}
