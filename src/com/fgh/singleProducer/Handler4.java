package com.fgh.singleProducer;

import java.util.Random;

import com.fgh.genrate1.Trade;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class Handler4 implements EventHandler<Trade>,WorkHandler<Trade> {

	@Override
	public void onEvent(Trade event) throws Exception {
		onEvent(event);
	}

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("handler4 set price:");
		event.setPrice(new Random().nextDouble()*999);
	}

}
