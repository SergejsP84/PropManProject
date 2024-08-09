package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.NumericDataMapping;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

import java.io.IOException;

public interface CardDataSaverService {
    boolean writeNDMToFile(NumericDataMapping mapping) throws IOException;
    NumericDataMapping extractNDMFromFile(UserType userType, Long userId) throws IOException;
    void removeNDMRecordFromFile(UserType userType, Long userId) throws Exception;
}
