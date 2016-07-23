package com.fgh.multiProducer;

import com.lmax.disruptor.RingBuffer;

/**
 * 生产者
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
	 * 用来发布事件，每调用一次发布一次事件
	 * @param id
	 */
	public void onData(String data) {
		//可以把RingBuffer看做一个事件队列，那么next就是得到下面的一个事件槽
		long sequence = ringBuffer.next();
		try{
			//用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
			Order order = ringBuffer.get(sequence);
			//获取要通过事件传递的业务数据
			order.setId(data);
		}finally{
			//发布事件
			//注意：最后的publish方法必须放在finally中 确保必须得到调用，如果 某个请求的sequence未被提交，将
			ringBuffer.publish(sequence);
		}
		System.out.println();
	}

}
