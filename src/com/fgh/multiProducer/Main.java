package com.fgh.multiProducer;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 业务比较简单，线路单一， 用RingBuffer 业务复杂，用Disruptor
 * 
 * @author Administrator
 *
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		RingBuffer<Order> ringBuffer = RingBuffer.create(ProducerType.MULTI, new EventFactory<Order>() {

			@Override
			public Order newInstance() {
				return new Order();
			}
		}, 1024 * 1024, new YieldingWaitStrategy());
		
		SequenceBarrier barrier = ringBuffer.newBarrier();
		
		Consumer[] consumers = new Consumer[3];
		for(int i=0;i<consumers.length;i++){
			consumers[i] = new Consumer("c"+i);
		}
		
		
		WorkerPool<Order> workerPool = new WorkerPool<Order>(ringBuffer, barrier, new IgnoreExceptionHandler(), consumers);
		
		ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
		workerPool.start(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
		
		final CountDownLatch latch = new CountDownLatch(1);
		for(int i=0;i<100;i++){
			final Producer producer = new Producer(ringBuffer);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					for(int j=0;j<100;j++){
						producer.onData(UUID.randomUUID().toString());
					}
				}
			}).start();
		}
		
		Thread.sleep(2000);
		
		System.out.println("开始生产数据...");
		latch.countDown();
		
		Thread.sleep(3000);
		
		System.out.println("总数:"+consumers[0].getCount());
		
		
		
	}
	
	static class IntEventExceptionHandler implements ExceptionHandler{

		@Override
		public void handleEventException(Throwable ex, long sequence, Object event) {
			
		}

		@Override
		public void handleOnStartException(Throwable ex) {
			
		}

		@Override
		public void handleOnShutdownException(Throwable ex) {
			
		}
		
	}
}
