package com.empbatchserver.job.notification;

import com.empbatchserver.adapter.message.KakaoTalkMessageAdapter;
import com.empbatchserver.repository.notification.NotificationEntity;
import com.empbatchserver.repository.notification.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SendNotificationItemWriter implements ItemWriter<NotificationEntity> {
    private final NotificationRepository notificationRepository;
    private final KakaoTalkMessageAdapter kakaoTalkMessageAdapter;

    public SendNotificationItemWriter(NotificationRepository notificationRepository, KakaoTalkMessageAdapter kakaoTalkMessageAdapter) {
        this.notificationRepository = notificationRepository;
        this.kakaoTalkMessageAdapter = kakaoTalkMessageAdapter;
    }

    @Override
    public void write(Chunk<? extends NotificationEntity> items) throws Exception {
        int count = 0;

        for (NotificationEntity item : items) {
            boolean successful = kakaoTalkMessageAdapter.sendKakaoTalkMessage(item.getUuid(), item.getText());

            if (successful) {
                item.updateSent(true);
                item.updateSentAt(LocalDateTime.now());
                notificationRepository.save(item);
                count++;
            }
        }

        log.info("SendNotificationItemWriter - write: 수업 전 알람 {}/{}건 전송 성공", count, items.size());
    }
}
