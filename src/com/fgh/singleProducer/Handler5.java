package com.fgh.singleProducer;

import com.fgh.genrate1.Trade;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class Handler5 implements EventHandler<Trade>,WorkHandler<Trade> {

	@Override
	public void onEvent(Trade event) throws Exception {
		onEvent(event);
	}

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("h5 get price" + event.getPrice());
		System.out.println("handler5 name:" + event.getName() + ",price:" + event.getPrice()+3 + ",instance=" + event);

	}

}
