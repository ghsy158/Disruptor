package com.fgh.genrate1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;

/**
 * WorkPool
 * 
 * @author Administrator
 *
 */
public class Main2 {

	private static final int BUFFER_SIZE = 1024;
	private static final int THREA_NUMBERS = 4;

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		// 创建一个单生产者的RingBuffer
		// 第一个参数叫EventFactory，职责是产生数据填充RingBuffer的区块
		// 第二个参数 缓冲区的大小 他必须是2的指数倍 目的是为了将求模运算转为&运算提高效率
		// 第三个是RingBuffer的生产者在没有可用区块的时候（可能是消费者 太慢了 ）的等待策略
		final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {

			@Override
			public Trade newInstance() {
				return new Trade();
			}

		}, BUFFER_SIZE, new YieldingWaitStrategy());

		// 创建线程池
		ExecutorService executors = Executors.newFixedThreadPool(THREA_NUMBERS);

		// 创建SequenceBarrier
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

		WorkHandler<Trade> handler = new TradeHandler();
		WorkerPool<Trade> workerPool = new WorkerPool<Trade>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(),
				handler);
		
		workerPool.start(executors);
		for (int i = 0; i < 8; i++) {
			long seq = ringBuffer.next();// ringBuffer的一个可用区域
			ringBuffer.get(seq).setPrice(Math.random() * 9999);// 给这个区域放入数据
			ringBuffer.publish(seq);// 发布这个区域块的数据 使hanler（消费者）可见
		}
		
		
//		// 创建消息处理器
//		BatchEventProcessor<Trade> transProcessor = new BatchEventProcessor<Trade>(ringBuffer, sequenceBarrier,
//				new TradeHandler());
//
//		// 这一步的目的是把消费者的位置信息引用注入到生产者 如果只有一个消费者可以忽略
//		ringBuffer.addGatingSequences(transProcessor.getSequence());
//
//		// 把消息处理器提交到线程池
//		executors.submit(transProcessor);
//
//		// 如果 存在多个消费者 重复执行上面的3行代码 新版本不需要
//		Future<?> future = executors.submit(new Callable<Void>() {
//
//			@Override
//			public Void call() throws Exception {
//				long seq;
//				for (int i = 0; i < 10; i++) {
//					seq = ringBuffer.next();// ringBuffer的一个可用区域
//					ringBuffer.get(seq).setPrice(Math.random() * 9999);// 给这个区域放入数据
//					ringBuffer.publish(seq);// 发布这个区域块的数据 使hanler（消费者）可见
//				}
//				return null;
//			}
//		});
//
//		future.get();
//		Thread.sleep(1000);// 等上1秒 等消费者处理完成
//		transProcessor.halt();// 通知事件处理器，可以结束了（并不是马上结束）
		executors.shutdown();
	}
}
