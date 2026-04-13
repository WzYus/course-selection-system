package cn.detect.cs;

import cn.detect.cs.service.CourseSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CourseSyncService courseSyncService;

    @Override
    public void run(String... args) {
        courseSyncService.syncAllCourses();
    }
}