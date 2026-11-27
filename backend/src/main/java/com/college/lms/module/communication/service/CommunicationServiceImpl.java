package com.college.lms.module.communication.service;

import com.college.lms.common.exception.ResourceNotFoundException;
import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.communication.dto.*;
import com.college.lms.module.communication.entity.*;
import com.college.lms.module.communication.repository.*;
import com.college.lms.module.course.entity.Course;
import com.college.lms.module.course.repository.CourseRepository;
import com.college.lms.module.user.entity.User;
import com.college.lms.module.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunicationServiceImpl implements CommunicationService {

    private final DiscussionThreadRepository threadRepository;
    private final DiscussionPostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final LiveClassSessionRepository liveClassRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CommunicationServiceImpl(DiscussionThreadRepository threadRepository,
                                    DiscussionPostRepository postRepository,
                                    NotificationRepository notificationRepository,
                                    LiveClassSessionRepository liveClassRepository,
                                    CourseRepository courseRepository,
                                    UserRepository userRepository) {
        this.threadRepository = threadRepository;
        this.postRepository = postRepository;
        this.notificationRepository = notificationRepository;
        this.liveClassRepository = liveClassRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscussionThreadDto> getCourseThreads(Long courseId) {
        return threadRepository.findByCourseIdOrderByIsPinnedDescCreatedAtDesc(courseId).stream()
                .map(this::mapToThreadSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DiscussionThreadDto getThreadDetails(Long threadId) {
        DiscussionThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("Thread not found: " + threadId));

        DiscussionThreadDto dto = mapToThreadSummary(thread);

        List<DiscussionPostDto> postDtos = postRepository.findByThreadIdOrderByCreatedAtAsc(threadId).stream()
                .map(p -> new DiscussionPostDto(
                        p.getId(),
                        p.getThread().getId(),
                        p.getAuthor().getId(),
                        p.getAuthor().getFirstName() + " " + p.getAuthor().getLastName(),
                        p.getAuthor().getRoles().isEmpty() ? "STUDENT" : p.getAuthor().getRoles().iterator().next().getName().name().replace("ROLE_", ""),
                        p.getContent(),
                        p.getCreatedAt()
                ))
                .collect(Collectors.toList());

        dto.setPosts(postDtos);
        return dto;
    }

    @Override
    @Transactional
    public DiscussionThreadDto createThread(Long courseId, String title, String content, UserPrincipal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        User author = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));

        DiscussionThread thread = new DiscussionThread(course, author, title, content, false);
        thread = threadRepository.save(thread);

        return mapToThreadSummary(thread);
    }

    @Override
    @Transactional
    public DiscussionPostDto addReply(Long threadId, String content, UserPrincipal principal) {
        DiscussionThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("Thread not found: " + threadId));

        User author = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getId()));

        DiscussionPost post = new DiscussionPost(thread, author, content);
        post = postRepository.save(post);

        return new DiscussionPostDto(
                post.getId(),
                thread.getId(),
                author.getId(),
                author.getFirstName() + " " + author.getLastName(),
                author.getRoles().isEmpty() ? "STUDENT" : author.getRoles().iterator().next().getName().name().replace("ROLE_", ""),
                post.getContent(),
                post.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(UserPrincipal principal) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(principal.getId()).stream()
                .map(n -> new NotificationDto(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.getNotificationType().name(),
                        n.getLinkUrl(),
                        n.getIsRead(),
                        n.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiveClassDto> getLiveClasses(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        return liveClassRepository.findByCourseIdOrderByScheduledStartDesc(courseId).stream()
                .map(lc -> new LiveClassDto(
                        lc.getId(),
                        course.getId(),
                        course.getCode(),
                        lc.getTitle(),
                        lc.getMeetingUrl(),
                        lc.getScheduledStart(),
                        lc.getDurationMinutes(),
                        lc.getIsActive()
                ))
                .collect(Collectors.toList());
    }

    private DiscussionThreadDto mapToThreadSummary(DiscussionThread thread) {
        DiscussionThreadDto dto = new DiscussionThreadDto();
        dto.setId(thread.getId());
        dto.setCourseId(thread.getCourse().getId());
        dto.setCourseCode(thread.getCourse().getCode());
        dto.setAuthorId(thread.getAuthor().getId());
        dto.setAuthorName(thread.getAuthor().getFirstName() + " " + thread.getAuthor().getLastName());
        dto.setAuthorRole(thread.getAuthor().getRoles().isEmpty() ? "STUDENT" : thread.getAuthor().getRoles().iterator().next().getName().name().replace("ROLE_", ""));
        dto.setTitle(thread.getTitle());
        dto.setContent(thread.getContent());
        dto.setIsPinned(thread.getIsPinned());
        dto.setIsLocked(thread.getIsLocked());
        dto.setCreatedAt(thread.getCreatedAt());
        dto.setReplyCount(postRepository.findByThreadIdOrderByCreatedAtAsc(thread.getId()).size());
        return dto;
    }
}
