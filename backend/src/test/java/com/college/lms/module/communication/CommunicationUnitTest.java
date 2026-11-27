package com.college.lms.module.communication;

import com.college.lms.module.communication.dto.DiscussionPostDto;
import com.college.lms.module.communication.dto.DiscussionThreadDto;
import com.college.lms.module.communication.dto.LiveClassDto;
import com.college.lms.module.communication.dto.NotificationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommunicationUnitTest {

    @Test
    @DisplayName("Verify thread creation, reply structure, notifications, and live class links")
    void testCommunicationStructures() {
        DiscussionPostDto post = new DiscussionPostDto(
                1L, 10L, 2L, "Linus Torvalds", "INSTRUCTOR", "Office hours are scheduled for 2 PM.", Instant.now()
        );

        DiscussionThreadDto thread = new DiscussionThreadDto();
        thread.setId(10L);
        thread.setCourseCode("CS101");
        thread.setTitle("Office Hours Q&A");
        thread.setReplyCount(1);
        thread.setPosts(List.of(post));

        NotificationDto notif = new NotificationDto(
                100L, "New Live Lecture", "CS101 Workshop has started", "LIVE_CLASS", "/live", false, Instant.now()
        );

        LiveClassDto liveClass = new LiveClassDto(
                5L, 1L, "CS101", "Live Problem Solving", "https://meet.google.com/test", Instant.now(), 60, true
        );

        assertEquals("CS101", thread.getCourseCode());
        assertEquals(1, thread.getPosts().size());
        assertEquals("Linus Torvalds", thread.getPosts().get(0).getAuthorName());
        assertFalse(notif.getIsRead());
        assertTrue(liveClass.getIsActive());
    }
}
