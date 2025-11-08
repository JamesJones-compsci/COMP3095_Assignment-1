package ca.gbc.comp3095.goaltrackingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Document(value="goal") // Associate goal with a particular name in db
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Goal {

    @Id // Primary key annotation
    private String goalId; // Inside POJO this is the primary key
    private String name;
    private String description;
    private GoalCategory category;
    private String frequency;
    private LocalDate startDate;
    private LocalDate targetDate;
    private String status;

    private double targetValue;
    private double currentValue;

    public double getProgressPercent() {
        if (targetValue <= 0) {
            return 0;
        }
        double percent = (currentValue / targetValue) * 100;
        if (percent > 100) {
            percent = 100;
        }
        return percent;
    }
    public void updateStatus() {
        if (getProgressPercent() >= 100) {
            this.status = "Completed - Excellent Effort";
        } else if (currentValue > 0) {
            this.status = "In Progress - Keep going";
        } else {
            this.status = "Not Yet Started - You Can Do It";
        }
    }

}
