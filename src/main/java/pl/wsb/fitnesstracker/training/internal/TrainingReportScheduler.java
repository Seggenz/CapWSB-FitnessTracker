package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.mail.api.EmailDto;
import pl.wsb.fitnesstracker.mail.api.EmailSender;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class TrainingReportScheduler {

    private final UserProvider userProvider;
    private final TrainingProvider trainingProvider;
    private final EmailSender emailSender;

    //@Scheduled(cron = "0 0 0 * * MON") // Every Monday at midnight
    @Scheduled(fixedRate = 60000)
    public void sendWeeklyTrainingReport() {
        log.info("Starting weekly training report generation");
        List<User> users = userProvider.findAllUsers();

        for (User user : users) {
            int trainingCount = trainingProvider.findAllTrainingsForUser(user.getId()).size();
            sendEmail(user, trainingCount);
        }
        log.info("Weekly training report generation finished");
    }

    private void sendEmail(User user, int trainingCount) {
        String subject = "Weekly Training Report";
        String content = "Hi " + user.getFirstName() + ",\n\n" +
                "Trainings in this week " + trainingCount + " .\n\n" +
                "\n";

        EmailDto emailDto = new EmailDto(user.getEmail(), subject, content);
        emailSender.send(emailDto);
    }
}
