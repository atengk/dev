package local.ateng.java.rocketmq.consumer;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@RocketMQMessageListener(
        topic = "order-topic",
        consumerGroup = "consumer-group-order",
        consumeMode = ConsumeMode.ORDERLY
)
@Component
public class OrderlyConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("顺序消息消费: " + message);
    }
}

