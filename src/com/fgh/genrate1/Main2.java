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

		// ����һ���������ߵ�RingBuffer
		// ��һ��������EventFactory��ְ���ǲ����������RingBuffer������
		// �ڶ������� �������Ĵ�С ��������2��ָ���� Ŀ����Ϊ�˽���ģ����תΪ&�������Ч��
		// ��������RingBuffer����������û�п��������ʱ�򣨿����������� ̫���� ���ĵȴ�����
		final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {

			@Override
			public Trade newInstance() {
				return new Trade();
			}

		}, BUFFER_SIZE, new YieldingWaitStrategy());

		// �����̳߳�
		ExecutorService executors = Executors.newFixedThreadPool(THREA_NUMBERS);

		// ����SequenceBarrier
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

		WorkHandler<Trade> handler = new TradeHandler();
		WorkerPool<Trade> workerPool = new WorkerPool<Trade>(ringBuffer, sequenceBarrier, new IgnoreExceptionHandler(),
				handler);
		
		workerPool.start(executors);
		for (int i = 0; i < 8; i++) {
			long seq = ringBuffer.next();// ringBuffer��һ����������
			ringBuffer.get(seq).setPrice(Math.random() * 9999);// ����������������
			ringBuffer.publish(seq);// ����������������� ʹhanler�������ߣ��ɼ�
		}
		
		
//		// ������Ϣ������
//		BatchEventProcessor<Trade> transProcessor = new BatchEventProcessor<Trade>(ringBuffer, sequenceBarrier,
//				new TradeHandler());
//
//		// ��һ����Ŀ���ǰ������ߵ�λ����Ϣ����ע�뵽������ ���ֻ��һ�������߿��Ժ���
//		ringBuffer.addGatingSequences(transProcessor.getSequence());
//
//		// ����Ϣ�������ύ���̳߳�
//		executors.submit(transProcessor);
//
//		// ��� ���ڶ�������� �ظ�ִ�������3�д��� �°汾����Ҫ
//		Future<?> future = executors.submit(new Callable<Void>() {
//
//			@Override
//			public Void call() throws Exception {
//				long seq;
//				for (int i = 0; i < 10; i++) {
//					seq = ringBuffer.next();// ringBuffer��һ����������
//					ringBuffer.get(seq).setPrice(Math.random() * 9999);// ����������������
//					ringBuffer.publish(seq);// ����������������� ʹhanler�������ߣ��ɼ�
//				}
//				return null;
//			}
//		});
//
//		future.get();
//		Thread.sleep(1000);// ����1�� �������ߴ������
//		transProcessor.halt();// ֪ͨ�¼������������Խ����ˣ����������Ͻ�����
		executors.shutdown();
	}
}
