package com.fgh.multiProducer;

import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * 消费者
 * @author Administrator
 *
 */
public class Consumer implements WorkHandler<Order>{

	private String consumerId;
	
	private static AtomicInteger count = new AtomicInteger(0);
	
	public Consumer(String consumerId) {
		this.consumerId = consumerId;
	}

	@Override
	public void onEvent(Order event) throws Exception {
		System.out.println("当前消费者:"+this.consumerId+",消费信息:"+event.getId());
		count.incrementAndGet();
	}

	public int getCount() {
		return count.get();
	}

	
}
