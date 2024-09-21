package de.paladinsinn.tp.dcis.operatives.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@JsonDeserialize(as = OperativeHistoryEntryImpl.class)
public interface OperativeHistoryEntry {
    @NotNull
    UUID getMissionUid();

    @NotNull
    String getMissionName();

    @NotNull
    OffsetDateTime getMissionDate();

    
    @NotNull
    SuccessState getSuccess();

    String getReport();

    long getXp();

    long getPayment();


    @NotNull
    OffsetDateTime getCreated();

    @Nullable
    OffsetDateTime getModified();

    @Nullable
    OffsetDateTime getDeleted();
}