package com.fgh.multiProducer;

import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * ������
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
		System.out.println("��ǰ������:"+this.consumerId+",������Ϣ:"+event.getId());
		count.incrementAndGet();
	}

	public int getCount() {
		return count.get();
	}

	
}
