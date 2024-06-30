package de.sky.regular.income.api.turnovers;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class TurnOverPreview {
    public String filename;
    public TurnoverImportFormat format;
    public String encoding;
    public ZonedDateTime uploadTime;
    public List<TurnoverRowPreview> rows;
}
