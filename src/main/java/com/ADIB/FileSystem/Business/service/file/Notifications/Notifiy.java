package com.ADIB.FileSystem.Business.service.file.Notifications;

import com.ADIB.FileSystem.Business.Enum.NOTIFICATIONTYPE;
import com.ADIB.FileSystem.Business.Model.Department;
import com.ADIB.FileSystem.Business.Model.File;
import com.ADIB.FileSystem.Business.Model.FileForward;
import com.ADIB.FileSystem.Business.Model.User;
import com.ADIB.FileSystem.Business.event.FileForwardedEvent;
import com.ADIB.FileSystem.DataAccess.repository.FileForwardRepo;
import com.ADIB.FileSystem.DataAccess.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Notifiy {
    private final FileForwardRepo fileForwardRepo;
    private final UserRepo userRepo;
    private final ApplicationEventPublisher eventPublisher;


    public void notifyDepartmentsOnUpload(File file, User uploader) {
        Set<Department> departments = file.getDepartments();
        if (departments == null || departments.isEmpty()) {
            return;
        }

        List<User> recipients = userRepo.findByDepartmentInAndIdNot(departments, uploader.getId());

        String message = "New file uploaded: " + file.getName();
        for (User recipient : recipients) {
            FileForward notification = FileForward.builder()
                    .file(file)
                    .sender(uploader)
                    .recipient(recipient)
                    .message(message)
                    .type(NOTIFICATIONTYPE.DEPARTMENT_UPLOAD)
                    .isRead(false)
                    .build();
            FileForward saved = fileForwardRepo.save(notification);
            eventPublisher.publishEvent(new FileForwardedEvent(this, saved));
        }
    }
    public void notifyManagerForApproval(File file, User manager) {
        FileForward notification = FileForward.builder()
                .file(file)
                .sender(file.getCreatedBy())
                .recipient(manager)
                .message("File pending your approval: " + file.getName())
                .type(NOTIFICATIONTYPE.APPROVAL_PENDING)
                .isRead(false)
                .build();
        FileForward saved = fileForwardRepo.save(notification);
        eventPublisher.publishEvent(new FileForwardedEvent(this, saved));
    }

    public void notifyEmployeesOnApproval(File file) {
        Set<Department> departments = file.getDepartments();
        if(departments == null || departments.isEmpty()) return;

        List<User> recipients = userRepo.findByDepartmentInAndIdNot(departments, file.getId());
        String message = "New file uploaded: " + file.getName();
        for (User recipient : recipients) {
            FileForward notification = FileForward.builder()
                    .file(file)
                    .sender(file.getCreatedBy())
                    .recipient(recipient)
                    .message(message)
                    .type(NOTIFICATIONTYPE.FILE_APPROVED)
                    .isRead(false)
                    .build();
            FileForward saved = fileForwardRepo.save(notification);
            eventPublisher.publishEvent(new FileForwardedEvent(this, saved));
        }

    }
}
