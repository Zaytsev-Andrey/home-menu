package ru.homemenu.recipeservice.database.util;

import ru.homemenu.recipeservice.database.entity.BaseUuidEntity;
import ru.homemenu.recipeservice.http.exception.OptimisticLockValidationException;

import java.util.Objects;

public class OptimisticLockUtil {

    public static void valid(BaseUuidEntity entity, Long actualVersion) {
        if (!Objects.equals(entity.getVersion(), actualVersion)) {
            throw new OptimisticLockValidationException(entity.getId(), entity.getVersion(), actualVersion);
        }
    }
}
