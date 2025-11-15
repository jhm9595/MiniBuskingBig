package com.minibuskingbig.chat.service;

import com.minibuskingbig.chat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcsContainerService {

    private final EcsClient ecsClient;
    private final ChatRoomService chatRoomService;

    @Value("${aws.ecs.cluster-name:minibusking-chat-cluster}")
    private String clusterName;

    @Value("${aws.ecs.task-definition:minibusking-chat-task}")
    private String taskDefinition;

    @Value("${aws.ecs.subnet-id}")
    private String subnetId;

    @Value("${aws.ecs.security-group-id}")
    private String securityGroupId;

    /**
     * 채팅 컨테이너 시작
     */
    public void startChatContainer(Long roomId) {
        try {
            log.info("Starting ECS container for room: {}", roomId);

            // 컨테이너 환경 변수 설정
            Map<String, String> environment = new HashMap<>();
            environment.put("ROOM_ID", String.valueOf(roomId));
            environment.put("REDIS_HOST", "redis-host"); // Redis 호스트 설정 필요

            // Task 실행
            RunTaskRequest runTaskRequest = RunTaskRequest.builder()
                .cluster(clusterName)
                .taskDefinition(taskDefinition)
                .launchType(LaunchType.FARGATE)
                .networkConfiguration(NetworkConfiguration.builder()
                    .awsvpcConfiguration(AwsVpcConfiguration.builder()
                        .subnets(subnetId)
                        .securityGroups(securityGroupId)
                        .assignPublicIp(AssignPublicIp.ENABLED)
                        .build())
                    .build())
                .count(1)
                .build();

            RunTaskResponse response = ecsClient.runTask(runTaskRequest);

            if (response.hasTasks() && !response.tasks().isEmpty()) {
                Task task = response.tasks().get(0);
                String taskArn = task.taskArn();
                String containerId = extractContainerIdFromArn(taskArn);

                // WebSocket URL 생성 (실제로는 로드 밸런서 또는 공개 IP 사용)
                String websocketUrl = String.format("ws://container-%s.minibusking.com/ws", containerId);

                // ChatRoom 활성화
                chatRoomService.activateChatRoom(roomId, containerId, taskArn, websocketUrl);

                log.info("ECS container started successfully for room: {} - ARN: {}", roomId, taskArn);
            } else {
                throw new RuntimeException("Failed to start ECS task");
            }

        } catch (Exception e) {
            log.error("Failed to start ECS container for room: {}", roomId, e);
            throw new RuntimeException("컨테이너 시작 실패", e);
        }
    }

    /**
     * 채팅 컨테이너 중지
     */
    public void stopChatContainer(Long roomId) {
        try {
            ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);

            if (chatRoom.getContainerArn() == null) {
                log.warn("No container ARN found for room: {}", roomId);
                return;
            }

            log.info("Stopping ECS container for room: {} - ARN: {}", roomId, chatRoom.getContainerArn());

            StopTaskRequest stopTaskRequest = StopTaskRequest.builder()
                .cluster(clusterName)
                .task(chatRoom.getContainerArn())
                .reason("Chat room closed")
                .build();

            ecsClient.stopTask(stopTaskRequest);

            // ChatRoom 종료
            chatRoomService.closeChatRoom(roomId);

            log.info("ECS container stopped successfully for room: {}", roomId);

        } catch (Exception e) {
            log.error("Failed to stop ECS container for room: {}", roomId, e);
            throw new RuntimeException("컨테이너 중지 실패", e);
        }
    }

    /**
     * 컨테이너 상태 확인
     */
    public String getContainerStatus(String taskArn) {
        try {
            DescribeTasksRequest request = DescribeTasksRequest.builder()
                .cluster(clusterName)
                .tasks(taskArn)
                .build();

            DescribeTasksResponse response = ecsClient.describeTasks(request);

            if (response.hasTasks() && !response.tasks().isEmpty()) {
                return response.tasks().get(0).lastStatus();
            }

            return "UNKNOWN";

        } catch (Exception e) {
            log.error("Failed to get container status for task: {}", taskArn, e);
            return "ERROR";
        }
    }

    /**
     * Task ARN에서 Container ID 추출
     */
    private String extractContainerIdFromArn(String taskArn) {
        // arn:aws:ecs:region:account-id:task/cluster-name/task-id
        String[] parts = taskArn.split("/");
        return parts[parts.length - 1];
    }

    /**
     * 컨테이너 비용 계산 (시간당)
     */
    public int calculateContainerCost(Long roomId) {
        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);

        if (chatRoom.getStartedAt() == null) {
            return 0;
        }

        // 시작 시간부터 현재까지 또는 종료 시간까지의 시간 계산
        java.time.LocalDateTime endTime = chatRoom.getEndedAt() != null
            ? chatRoom.getEndedAt()
            : java.time.LocalDateTime.now();

        long hours = java.time.Duration.between(chatRoom.getStartedAt(), endTime).toHours();

        // 최소 1시간 청구
        if (hours == 0) {
            hours = 1;
        }

        // 시간당 1,000원
        return (int) (hours * 1000);
    }
}
