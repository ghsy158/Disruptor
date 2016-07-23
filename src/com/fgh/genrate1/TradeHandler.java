package com.fgh.genrate1;

import java.util.UUID;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class TradeHandler implements EventHandler<Trade> ,WorkHandler<Trade>{

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		onEvent(event);
	}

	/**
	 * �����Ǿ���������߼�
	 */
	@Override
	public void onEvent(Trade event) throws Exception {
		
		event.setId(UUID.randomUUID().toString());
		System.out.println("����id:"+event.getId());
	}

}
