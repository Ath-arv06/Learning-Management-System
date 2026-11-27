package com.college.lms.module.communication.service;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.communication.dto.*;

import java.util.List;

public interface CommunicationService {
    List<DiscussionThreadDto> getCourseThreads(Long courseId);
    DiscussionThreadDto getThreadDetails(Long threadId);
    DiscussionThreadDto createThread(Long courseId, String title, String content, UserPrincipal principal);
    DiscussionPostDto addReply(Long threadId, String content, UserPrincipal principal);

    List<NotificationDto> getUserNotifications(UserPrincipal principal);
    void markNotificationAsRead(Long notificationId);

    List<LiveClassDto> getLiveClasses(Long courseId);
}
