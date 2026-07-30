package com.ADIB.FileSystem.Validation;

import com.ADIB.FileSystem.dto.request.FileSearchRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, FileSearchRequest> {

    @Override
    public boolean isValid(FileSearchRequest request, ConstraintValidatorContext ctx) {
        if(request.getFromDate() != null && request.getToDate() != null && request.getFromDate().isAfter(request.getToDate())) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("from Date must not be after to Date").addConstraintViolation();
            return false;
        }
        return true;
    }
}
