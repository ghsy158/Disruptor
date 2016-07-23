package com.fgh.multiProducer;

import com.lmax.disruptor.RingBuffer;

/**
 * ������
 * @author Administrator
 *
 */
public class Producer {

	RingBuffer<Order> ringBuffer;
	
 	public Producer(RingBuffer<Order> ringBuffer) {
 		this.ringBuffer = ringBuffer;
	}

	public RingBuffer<Order> getRingBuffer() {
		return ringBuffer;
	}

	public void setRingBuffer(RingBuffer<Order> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	/**
	 * ���������¼���ÿ����һ�η���һ���¼�
	 * @param id
	 */
	public void onData(String data) {
		//���԰�RingBuffer����һ���¼����У���ônext���ǵõ������һ���¼���
		long sequence = ringBuffer.next();
		try{
			//�����������ȡ��һ���յ��¼�������䣨��ȡ����Ŷ�Ӧ���¼�����
			Order order = ringBuffer.get(sequence);
			//��ȡҪͨ���¼����ݵ�ҵ������
			order.setId(data);
		}finally{
			//�����¼�
			//ע�⣺����publish�����������finally�� ȷ������õ����ã���� ĳ�������sequenceδ���ύ����
			ringBuffer.publish(sequence);
		}
		System.out.println();
	}

}
