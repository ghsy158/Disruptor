package com.fgh.singleProducer;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.fgh.genrate1.Trade;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

public class TradePublisher implements Runnable{

	Disruptor<Trade> disruptor;
	private CountDownLatch latch;
	
	private static final int LOOP= 10;
	
	public TradePublisher(Disruptor<Trade> disruptor, CountDownLatch latch) {
		super();
		this.disruptor = disruptor;
		this.latch = latch;
	}


	@Override
	public void run() {
		TradeEventTranslator tradeEventTranslator = new TradeEventTranslator();
		for(int i=0;i<LOOP;i++){
			disruptor.publishEvent(tradeEventTranslator);
		}
		latch.countDown();
	}

	/**
	 * Ìî³äÊý¾Ý
	 * @author Administrator
	 *
	 */
	class TradeEventTranslator implements EventTranslator<Trade>{

		private Random random = new Random();
		
		@Override
		public void translateTo(Trade event, long sequence) {
			this.genreateTrade(event);
		}
		
		private  Trade genreateTrade(Trade trade){
			trade.setPrice(random.nextDouble()*9999);
			return trade;
		}
	}
}
